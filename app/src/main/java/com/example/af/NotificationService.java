package com.example.af;

import static android.app.PendingIntent.FLAG_IMMUTABLE;
import static com.example.af.ApplicationManager.DEFAULT_NOTIFICATION_CHANNEL_ID;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class NotificationService extends Service {

   private AtomicInteger idCounter = new AtomicInteger(0);
   private ApplicationContext context = ApplicationContext.instance();
   private Database db = Database.instance();
   private Queue<Remedio> toSend = new ConcurrentLinkedQueue<>();
   private long SECONDS_IN_DAY = ChronoUnit.DAYS.getDuration().getSeconds();
   private NotificationManager notificationManager;
   private Thread queuingTask, notifyingTask;

   @Nullable
   @Override
   public IBinder onBind(Intent intent) {
      return null;
   }

   @Override
   public void onCreate() {
      super.onCreate();
      notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
   }
   @Override
   public void onDestroy() {
      super.onDestroy();
      if (queuingTask != null) {
         queuingTask.interrupt();
      }
      if (notifyingTask != null) {
         notifyingTask.interrupt();
      }
   }

   @Override
   public int onStartCommand(Intent intent, int flags, int startId) {
      startForeground(1, createForegroundNotification());
      queuingTask = createLoop(this::queueMedicines, 5000);
      notifyingTask = createLoop(this::notifyPending, 100);
      queuingTask.start();
      notifyingTask.start();
      return START_REDELIVER_INTENT;
   }

   private Thread createLoop(Runnable runnable, int reliefMillis) {
      return new Thread(() -> {
         try {
            while (!Thread.currentThread().isInterrupted()) {
               try {
                  runnable.run();
               } catch (Exception e) {
                  Log.e("NotificationService", "Error in worker task: " + e.getMessage(), e);
               }
               Thread.sleep(reliefMillis);
            }
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Log.i("NotificationService", "Worker thread interrupted and exiting.");
         }
      });
   }
   private void queueMedicines() {
      context.listaAtual
         .stream()
         .filter(r -> !r.notified)
         .filter(this::isTimeInsideTolerance)
         .forEach(n -> toSend.add(n));
   }
   private void notifyPending() {
      Remedio r = toSend.poll();
      if (r == null) return;
      Notification n = createNotification(r);
      notificationManager.notify(idCounter.incrementAndGet(), n);
      r.notified = true;
      db.salvarRemedio(r);
   }

   private Notification createForegroundNotification() {
      return new NotificationCompat.Builder(this, DEFAULT_NOTIFICATION_CHANNEL_ID)
         .setContentTitle("Medicine Service Active")
         .setContentText("Monitoring consumption times.")
         .setSmallIcon(R.drawable.pill)
         .setPriority(NotificationCompat.PRIORITY_MIN)
         .build();
   }
   private Notification createNotification(Remedio remedio) {
      String title = getString(R.string.notification_medicineTime_title);
      String content = getString(R.string.notification_medicineTime_body) + " " + remedio.nome;
      Intent intent = new Intent(this, PaginaListagemActivity.class);
      PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_IMMUTABLE);
      Notification notification = new NotificationCompat
         .Builder(this, DEFAULT_NOTIFICATION_CHANNEL_ID)
         .setContentTitle(title)
         .setContentText(content)
         .setSmallIcon(R.drawable.pill)
         .setContentIntent(pendingIntent)
         .setAutoCancel(true)
         .setPriority(NotificationCompat.PRIORITY_HIGH)
         .build();
      return notification;
   }

   private boolean isTimeInsideTolerance(Remedio remedio) {
      LocalTime now = context.now();
      LocalTime[] limits = calculateTimeTolerance(remedio.horarioDeConsumo, context.consumptionTimeTolerancePercentage);
      if (limits[0].isAfter(limits[1])) {
         return now.isAfter(limits[0]) || now.isBefore(limits[1]);
      } else {
         return now.isAfter(limits[0]) && now.isBefore(limits[1]);
      }
   }

   private LocalTime[] calculateTimeTolerance(LocalTime centerTime, double tolerancePercentage) {
      long toleranceSeconds = (long) (tolerancePercentage * SECONDS_IN_DAY);
      long centerTimeSeconds = centerTime.toSecondOfDay();
      long normalizedLowerLimitSeconds = ((centerTimeSeconds - toleranceSeconds) % SECONDS_IN_DAY + SECONDS_IN_DAY) % SECONDS_IN_DAY;
      long normalizedUpperLimitSeconds = (centerTimeSeconds + toleranceSeconds) % SECONDS_IN_DAY;
      LocalTime lowerLimitTime = LocalTime.ofSecondOfDay(normalizedLowerLimitSeconds);
      LocalTime upperLimitTime = LocalTime.ofSecondOfDay(normalizedUpperLimitSeconds);
      return new LocalTime[]{lowerLimitTime, upperLimitTime};
   }

}
