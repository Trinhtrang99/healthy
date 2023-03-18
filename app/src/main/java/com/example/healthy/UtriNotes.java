package com.example.healthy;

import android.content.Context;
import android.util.Log;

import androidx.annotation.WorkerThread;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class UtriNotes {
    private static UtriNotes instance = null;

    public static UtriNotes getInstance() {
        if (instance == null) {
            instance = new UtriNotes();
            return instance;
        }

        return instance;
    }

    @WorkerThread
    public Collection<String> getNodes(Context context) {
        HashSet<String> results = new HashSet<>();
        Task<List<Node>> nodeListTask =
                Wearable.getNodeClient(context).getConnectedNodes();
        try {
            // Block on a task and get the result synchronously (because this is on a background
            // thread).
            List<Node> nodes = Tasks.await(nodeListTask);
            for (Node node : nodes) {
                results.add(node.getId());
            }
        } catch (ExecutionException exception) {
            Log.e("TAG", "Task failed: " + exception);
        } catch (InterruptedException exception) {
            Log.e("TAG", "Interrupt occurred: " + exception);
        }
        return results;
    }

    @WorkerThread
    public void sendStartActivityMessage(String node, String event, Context context) {

        Task<Integer> sendMessageTask =
                Wearable.getMessageClient(context).sendMessage(node, "/APP_OPEN_WEARABLE_PAYLOAD", new byte[0]);

        try {

            Integer result = Tasks.await(sendMessageTask);

        } catch (ExecutionException exception) {
            Log.e("TAG", "Task failed: " + exception);

        } catch (InterruptedException exception) {
            Log.e("TAG", "Interrupt occurred: " + exception);
        }
    }

    public Double cacularRMR(String gender, float kg, float cm, int old, Context context) {
        double RMR = 0;
        if (gender.equals(context.getString(R.string.nu))) {
            RMR = 9.99 * kg + 6.25 * cm - 4.92 * old - 161;
        } else {
            RMR = 9.99 * kg + 6.25 * cm - 4.92 * old + 5;
        }
        return RMR;
    }

    public Double cacularWithOcc(String occu, Double RMR, Context context) {

        if (occu.equals(context.getString(R.string.it_vd))) {
            return RMR * 1.2;
        } else if (occu.equals(context.getString(R.string.vd_nhe))) {
            return RMR * 1.375;

        } else if (occu.equals(context.getString(R.string.vd_vua_phai))) {
            return RMR * 1.55;

        } else if (occu.equals(context.getString(R.string.vd_nhieu))) {
            return RMR * 1.725;

        } else if (occu.equals(context.getString(R.string.vd_cao))) {
            return RMR * 1.9;

        }
        return RMR;

    }
}
