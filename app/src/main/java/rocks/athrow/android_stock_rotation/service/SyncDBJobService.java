package rocks.athrow.android_stock_rotation.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.UUID;

import rocks.athrow.android_stock_rotation.data.SyncDB;

/**
 * SyncDBJobService
 * Created by joselopez on 1/26/17.
 */

public class SyncDBJobService extends JobService {
    private static final String LOG_TAG = "SyncDBService";
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.e(LOG_TAG, "------------------ onStartJob 1 ---------------------" );
        Log.e(LOG_TAG, "onStartJob 1 " + true + " " + UUID.randomUUID());
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
        SynDBAsyncTask() {
            Log.e(LOG_TAG, "Creating SyncDBAsyncTask");
        }

        @Override
        protected JobParameters[] doInBackground(JobParameters... params) {
            Log.e(LOG_TAG, "Running SyncDB.downloadNewRecords()");
            Context context = getApplicationContext();
            SyncDB.postTransfers(context);
            SyncDB.downloadNewRecords(context);
            return params;
        }

        @Override
        protected void onPostExecute(JobParameters[] result) {
            for (JobParameters params : result) {
                    Log.e(LOG_TAG, "Finishing Job # " + params.getJobId());
                    Log.e(LOG_TAG, "------------------ onPostExecute ---------------------" );
                    jobFinished(params, true);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Log.e(LOG_TAG, "SynDBAsyncTask Cancelled");
        }
    }
}
