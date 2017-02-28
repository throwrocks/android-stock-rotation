package rocks.athrow.android_stock_rotation.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.os.AsyncTask;

import rocks.athrow.android_stock_rotation.data.SyncDB;

/**
 * SyncDBJobService
 * Created by joselopez on 1/26/17.
 */

public class SyncDBJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        SynDBAsyncTask synDBAsyncTask = new SynDBAsyncTask();
        synDBAsyncTask.execute(jobParameters);
        return true;
    }


    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }

    private class SynDBAsyncTask extends AsyncTask<JobParameters, Void, JobParameters[]> {
        SynDBAsyncTask() {
        }

        @Override
        protected JobParameters[] doInBackground(JobParameters... params) {
            Context context = getApplicationContext();
            SyncDB.postTransfers(context);
            SyncDB.downloadNewRecords(context);
            return params;
        }

        @Override
        protected void onPostExecute(JobParameters[] result) {
            for (JobParameters params : result) {
                jobFinished(params, true);
            }
        }
    }
}
