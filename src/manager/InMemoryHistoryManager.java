package manager;

import tasks.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager{

    private static class Node {
        Task task;
        Node prev;
        Node next;

        private Node (Node prev, Task task, Node next) {
            this.prev = prev;
            this.task = task;
            this.next = next;
        }
    }

    private final Map<Integer, Node> nodeMap = new HashMap<>();
    private Node first = null;
    private Node last = null;

    @Override
    public List<Task> getHistory(){
        return getTasks();
    }

    @Override
    public void addTask(Task task) {
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        Node node = nodeMap.get(id);
        removeNode(node);
    }

    private List<Task> getTasks() {
        List<Task> tasksResult = new ArrayList<>();
        Node node = first;
        while (node != null) {
            tasksResult.add(node.task);
            node = node.next;
        }
        return tasksResult;
    }

    private void linkLast(Task task) {
        final Node l = last;
        final Node newNode = new Node(l, task, null);
        last = newNode;
        if (l == null)
            first = newNode;
        else
            l.next = newNode;

        removeNode(newNode);
        nodeMap.put(task.getId(), newNode);
    }

    private void removeNode(Node node) {
        final Node nodeRem = nodeMap.remove(node.task.getId());
        if (nodeRem == null) {
            return;
        }

        if (nodeRem.prev == null && nodeRem.next != null) {
            first = nodeRem.next;
            nodeRem.next.prev = null;
        } else if (nodeRem.prev != null && nodeRem.next != null) {
            nodeRem.prev.next = nodeRem.next;
            nodeRem.next.prev = nodeRem.prev;
        } else if (nodeRem.prev != null && nodeRem.next == null) {
            last = nodeRem.prev;
            last.next = null;
        }
    }
}
