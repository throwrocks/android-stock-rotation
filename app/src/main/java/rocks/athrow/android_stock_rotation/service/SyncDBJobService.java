package rocks.athrow.android_stock_rotation.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.util.Log;

import java.util.UUID;

import rocks.athrow.android_stock_rotation.data.SyncDB;


/**
 * Created by joselopez on 1/26/17.
 */

public class SyncDBJobService extends JobService {
    private static final String LOG_TAG = "SyncDBService";
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.e(LOG_TAG, "------------------ onStartJob ---------------------" );
        Log.e(LOG_TAG, "onStartJob " + true + " " + UUID.randomUUID());
        SynDBAsyncTask synDBAsyncTask = new SynDBAsyncTask();
        synDBAsyncTask.execute(jobParameters);
        return true;
    }


    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.e(LOG_TAG, "onStopJob " + true);
        return true;
    }

    private class SynDBAsyncTask extends AsyncTask<JobParameters, Void, JobParameters[]> {


        @Override
        protected JobParameters[] doInBackground(JobParameters... params) {
            Log.e(LOG_TAG, "Running SyncDB.sync()");
            SyncDB.sync(getApplicationContext());
            return params;
        }

        @Override
        protected void onPostExecute(JobParameters[] result) {
            for (JobParameters params : result) {
                if (!hasJobBeenStopped(params)) {
                    Log.e(LOG_TAG, "Finishing job with id=" + params.getJobId());
                    Log.e(LOG_TAG, "------------------ onPostExecute ---------------------" );
                    jobFinished(params, true);
                }
            }
        }

        private boolean hasJobBeenStopped(JobParameters params) {
            // TODO: Logic for checking stop.
            return false;
        }

        public boolean stopJob(JobParameters params) {
            Log.e(LOG_TAG, "stopJob id=" + params.getJobId());
            // TODO: Logic for stopping a job. return true if job should be rescheduled.
            return false;
        }

    }
}
