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
}
