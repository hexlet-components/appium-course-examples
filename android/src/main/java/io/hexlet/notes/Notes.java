package io.hexlet.notes;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.*;
import android.view.View;
import org.json.JSONArray;
import java.util.ArrayList;

public class Notes extends Activity {
    private LinearLayout page;
    private final ArrayList<String> records = new ArrayList<>();
    private final Handler handler = new Handler();
    private int delay;
    private final int[] delays = {0, 2000, 4000, -1};

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        try {
            JSONArray saved = new JSONArray(getPreferences(0).getString("records", "[]"));
            for (int i = 0; i < saved.length(); i++) records.add(saved.getString(i));
        } catch (org.json.JSONException e) {
            throw new IllegalStateException(e);
        }
        form(-1);
    }

    private void screen(String title) {
        setTitle(title);
        page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        setContentView(page);
    }

    private Button button(int id, String text, View.OnClickListener action) {
        Button button = new Button(this);
        button.setId(id);
        button.setText(text);
        button.setOnClickListener(action);
        page.addView(button);
        return button;
    }

    private void persist() {
        getPreferences(0).edit().putString("records", new JSONArray(records).toString()).apply();
    }

    private String delayText() {
        return delays[delay] < 0 ? "No response" : "Delay " + delays[delay] + " ms";
    }

    private void form(int index) {
        screen("Edit note");
        EditText input = new EditText(this);
        input.setId(R.id.title_input);
        input.setSingleLine();
        input.setHint("Title");
        if (index >= 0) input.setText(records.get(index));
        page.addView(input);
        button(R.id.save, getString(R.string.save_label), v -> {
            String title = input.getText().toString();
            if (title.isEmpty()) { input.setError("Enter a title"); return; }
            screen("Saving");
            Runnable saved = () -> {
                if (index < 0) records.add(title); else records.set(index, title);
                persist();
                screen("Saved");
                TextView result = new TextView(this);
                result.setId(R.id.result);
                result.setText(title);
                page.addView(result);
                button(R.id.open_list, "Records", w -> list());
            };
            if (delays[delay] == 0) saved.run();
            if (delays[delay] > 0) handler.postDelayed(saved, delays[delay]);
        });
        button(R.id.delay, delayText(), v -> {
            delay = (delay + 1) % delays.length;
            ((Button) v).setText(delayText());
        });
        button(R.id.open_list, "Records", v -> list());
    }

    private void list() {
        screen("Records");
        button(R.id.new_note, "New note", v -> form(-1));
        button(R.id.seed, "Load 80 records", v -> {
            records.clear();
            for (int i = 1; i <= 80; i++) records.add("Note " + i);
            persist();
            list();
        });
        ListView list = new ListView(this);
        list.setId(R.id.records);
        list.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, records));
        list.setOnItemClickListener((a, v, index, id) -> form(index));
        page.addView(list);
    }

    @Override protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
