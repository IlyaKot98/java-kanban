package manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import tasks.Task;

public class JsonManager {

    public static String tasksToJson (Task task) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();

        return gson.toJson(task);

    }

}
