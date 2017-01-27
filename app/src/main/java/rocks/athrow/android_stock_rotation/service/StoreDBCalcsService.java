package rocks.athrow.android_stock_rotation.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.util.Log;

import java.util.UUID;

import rocks.athrow.android_stock_rotation.data.SyncDB;

/**
 * StoreDBCalcsService
 * Created by jose on 1/26/17.
 */

public class StoreDBCalcsService extends JobService {
    private static final String LOG_TAG = "StoreCalcsDBService";
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.e(LOG_TAG, "------------------ onStartJob 2 ---------------------" );
        Log.e(LOG_TAG, "onStartJob 2 " + true + " " + UUID.randomUUID());
        StoreDBCalcsAsyncTask synDBAsyncTask = new StoreDBCalcsAsyncTask();
        synDBAsyncTask.execute(params);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.e(LOG_TAG, "onStopJob " + true);
        return true;
    }

    private class StoreDBCalcsAsyncTask extends AsyncTask<JobParameters, Void, JobParameters[]> {
        public StoreDBCalcsAsyncTask() {
            Log.e(LOG_TAG, "Creating StoreDBCalcsAsyncTask");
        }

        @Override
        protected JobParameters[] doInBackground(JobParameters... params) {
            Log.e(LOG_TAG, "Running SyncDB.storeCalcs()");
            SyncDB.storeCalcs(getApplicationContext());
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
            Log.e(LOG_TAG, "StoreDBCalcsAsyncTask Cancelled");
        }
    }
}
