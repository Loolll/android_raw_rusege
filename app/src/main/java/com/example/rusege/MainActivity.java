package com.example.rusege;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.ContentView;
import androidx.annotation.Size;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.security.InvalidKeyException;
import java.security.KeyException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.util.Random;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity {
    private static long back_pressed;
    public Map<String, List<Task>> data;
    public Map<String, Task> data_via_id;
    public List<String> history_by_id = new ArrayList<>();
    public List<Task> tasks;
    public Task task;
    public Boolean certain = false;
    public Random generator = new Random();
    Dialog to_quest_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        data = getData();
        //findViewById(R.id.question);

        to_quest_dialog = new Dialog(MainActivity.this);
        to_quest_dialog.setTitle("Введите ID задания");
        to_quest_dialog.setContentView(R.layout.dialog_to_quest);

    }

    public void onToQuestButtonClick(View view) {
        to_quest_dialog.show();
    }

    public void onToRulesButtonClick(View view) {
        setContentView(R.layout.rules_main);
    }

    @SuppressLint("SetTextI18n")
    public void onRuleTaskButtonClick(View view){
        setContentView(R.layout.rules_task);
        int task_number = IdToNumber(view.getId());
        TextView inRuleTaskNumber = findViewById(R.id.inRuleTaskNumber);
        TextView ruleText = findViewById(R.id.ruleText);
        inRuleTaskNumber.setText("Задание №"+task_number);
        switch (task_number){
            case 1:
                ruleText.setText("Задание 1 требует от учащегося умения проводить информационную обработку текста.\nВ нём всегда небольшой объём, всегда только три предложения и всегда два верных ответа.\nЭто задание, как и 2-е, проверяет способность учащихся улавливать логику развития мысли автора предъявленного для анализа текста. При этом экзаменуемые должны иметь представление о том, что одну и ту же информацию можно изложить, используя разные синтаксические конструкции, и задание 1 контрольных измерительных материалов нацеливает учащихся на использование всего богатства синтаксических конструкций, которыми располагает родной язык.\nЧтобы решить задание 1, необходимо выделить главную информацию предлагаемого текста. Затем:\n— Сжать эту информацию в одно предложение самому;\n— Найти хотя бы одно предложение, в котором есть, на Ваш взгляд, ВСЯ информация, и сравнить с тем, что получилось у Вас;\n— Обратить внимание на то, что в ТРЁХ из пяти предложений информация будет:\nа) искажать текст, внося в него дополнения или нарушая причинно-следственные связи;\nб) неполной, то есть будет передавать содержание верно, но лишь частично;\n" +
                        "в) слишком краткой.\n\n\nДалее находим предложение, как две капли воды похожее по смыслу на вычисленное нами. Та же информация. Те же факты. Но — другими синтаксическими конструкциями. Например, придаточное определительное будет заменено причастным оборотом. Однородные сказуемые — деепричастными оборотами и т. п.");
                break;
            case 2:
            case 3:
            case 4:
            default:
                ruleText.setText("KEK RULE");
        }
    }

    @SuppressLint("SetTextI18n")
    public void onInDialogToQuestButtonClick(View view) {
        EditText et_id = (EditText) to_quest_dialog.findViewById(R.id.inDialogID);
        if (!data_via_id.containsKey(et_id.getText().toString().toLowerCase())) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("!");
            alertDialog.setMessage("Задания с данным ID не существует. (либо базы устарели)");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        } else {
            Task task = data_via_id.get(et_id.getText().toString().toLowerCase());
            setContentView(R.layout.activity_task);
            RenderTaskPage(Integer.parseInt(task.task_number), view);
            RenderTask(view, task);
            to_quest_dialog.cancel();
        }

    }

    @Override
    public void onBackPressed() {
        if (back_pressed + 400 > System.currentTimeMillis())
            super.onBackPressed();
        else
            goHome();
        back_pressed = System.currentTimeMillis();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickHistoryButton(View view) {
        setContentView(R.layout.activity_history);
        TextView historyIDText = findViewById(R.id.historyIdView);
        historyIDText.setText("" + String.join(", ", history_by_id));
    }

    public void onClickStart(View view) {
        RenderTaskPage(view.getId(), view);
        NextTaskGenerate(view);
    }

    public void onToQuestsButtonClick(View view) {
        setContentView(R.layout.activity_main);
    }

    public void onClickNext(View view) {
        NextTaskGenerate(view);
    }

    public void onClickCheck(View view) {
        EditText et_answer = (EditText) findViewById(R.id.answer);
        String your_answer = et_answer.getText().toString().toLowerCase();
        String[] right_answers = task.answer.toLowerCase().substring(7).split("\\|");
        Button check = findViewById(R.id.check);
        TextView solution = findViewById(R.id.solution);
        TextView right_answer = findViewById(R.id.right_answer);

        if (Arrays.asList(right_answers).contains(your_answer)) {
            check.setBackgroundColor(Color.rgb(0, 255, 0));
        } else {
            check.setBackgroundColor(Color.rgb(255, 0, 0));
        }
        solution.setVisibility(View.VISIBLE);
        right_answer.setVisibility(View.VISIBLE);
    }

    @SuppressLint("SetTextI18n")
    public void RenderTask(View view, Task task) {
        TextView question = findViewById(R.id.question);
        TextView solution = findViewById(R.id.solution);
        TextView ID = findViewById(R.id.task_id);
        TextView right_answer = findViewById(R.id.right_answer);
        Button check = findViewById(R.id.check);
        EditText et_answer = (EditText) findViewById(R.id.answer);

        question.setText(task.question);
        solution.setText(task.solution);
        ID.setText("ID" + task.task_id);
        right_answer.setText(task.answer);
        check.setBackgroundColor(Color.rgb(0, 150, 136));
        solution.setVisibility(View.GONE);
        right_answer.setVisibility(View.GONE);
        et_answer.setText("");

        history_by_id.add(task.task_id);
    }

    public void NextTaskGenerate(View view) {
        if (certain) RenderTaskPage(100, view);
        int rnd_index = generator.nextInt(tasks.size());
        task = tasks.get(rnd_index);
        RenderTask(view, task);
    }

    @SuppressLint("SetTextI18n")
    public void RenderTaskPage(int id, View view) {
        int task_number = IdToNumber(id);
        if (task_number == 100) {
            task_number = (generator.nextInt(26) + 1);
            certain = true;
            tasks = data.get(Integer.toString(task_number));
        }
        setContentView(R.layout.activity_task);
        TextView label = findViewById(R.id.task_label);
        label.setText("Задание №" + task_number);
        tasks = data.get(Integer.toString(task_number));
    }

    public Map<String, List<Task>> getData() {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, List<Task>> data_new = new HashMap<>();
        Map<String, Task> data_new_id = new HashMap<>();
        Map<String, List<Map<String, String>>> temp_data = new HashMap<>();
        try {
            InputStream ins = getResources().openRawResource(
                    getResources().getIdentifier("russian",
                            "raw", getPackageName()));
            temp_data = mapper.readValue(ins, Map.class);
        } catch (IOException ex) {
        }
        for (Map.Entry<String, List<Map<String, String>>> item : temp_data.entrySet()) {
            List<Map<String, String>> task_for_cur = item.getValue();
            List<Task> tsk = new ArrayList<>();
            for (int i = 0; i < task_for_cur.size(); i++) {
                Task task = new Task(task_for_cur.get(i), item.getKey());
                tsk.add(task);
                data_new_id.put(task.task_id, task);
            }
            data_new.put(item.getKey(), tsk);
            //data.put(item.getKey(), tsk);
        }
        data_via_id = data_new_id;
        return data_new;
    }

    @SuppressLint("NonConstantResourceId")
    public int IdToNumber(int id) {
        switch (id) {
            case R.id.task1:
            case R.id.ruleTaskButton1:
            case 1:
                return 1;
            case R.id.task2:
            case R.id.ruleTaskButton2:
            case 2:
                return 2;
            case R.id.task3:
            case R.id.ruleTaskButton3:
            case 3:
                return 3;
            case R.id.task4:
            case R.id.ruleTaskButton4:
            case 4:
                return 4;
            case R.id.task5:
            case R.id.ruleTaskButton5:
            case 5:
                return 5;
            case R.id.task6:
            case R.id.ruleTaskButton6:
            case 6:
                return 6;
            case R.id.task7:
            case R.id.ruleTaskButton7:
            case 7:
                return 7;
            case R.id.task8:
            case R.id.ruleTaskButton8:
            case 8:
                return 8;
            case R.id.task9:
            case R.id.ruleTaskButton9:
            case 9:
                return 9;
            case R.id.task10:
            case R.id.ruleTaskButton10:
            case 10:
                return 10;
            case R.id.task11:
            case R.id.ruleTaskButton11:
            case 11:
                return 11;
            case R.id.task12:
            case R.id.ruleTaskButton12:
            case 12:
                return 12;
            case R.id.task13:
            case R.id.ruleTaskButton13:
            case 13:
                return 13;
            case R.id.task14:
            case R.id.ruleTaskButton14:
            case 14:
                return 14;
            case R.id.task15:
            case R.id.ruleTaskButton15:
            case 15:
                return 15;
            case R.id.task16:
            case R.id.ruleTaskButton16:
            case 16:
                return 16;
            case R.id.task17:
            case R.id.ruleTaskButton17:
            case 17:
                return 17;
            case R.id.task18:
            case R.id.ruleTaskButton18:
            case 18:
                return 18;
            case R.id.task19:
            case R.id.ruleTaskButton19:
            case 19:
                return 19;
            case R.id.task20:
            case R.id.ruleTaskButton20:
            case 20:
                return 20;
            case R.id.task21:
            case R.id.ruleTaskButton21:
            case 21:
                return 21;
            case R.id.task22:
            case R.id.ruleTaskButton22:
            case 22:
                return 22;
            case R.id.task23:
            case R.id.ruleTaskButton23:
            case 23:
                return 23;
            case R.id.task24:
            case R.id.ruleTaskButton24:
            case 24:
                return 24;
            case R.id.task25:
            case R.id.ruleTaskButton25:
            case 25:
                return 25;
            case R.id.task26:
            case R.id.ruleTaskButton26:
            case 26:
                return 26;
            default:
                return 100;
        }
    }

    public void goHomeButton(View view) {
        goHome();
    }

    public void goHome() {
        setContentView(R.layout.activity_home);
        certain = false;
    }

}


class Task {
    public String question;
    public String answer;
    public String solution;
    public String task_id;
    public String task_number;

    public Task(Map<String, String> map, String task_number) {
        this.answer = map.get("answer");
        this.question = map.get("question");
        this.solution = map.get("solution");
        this.task_id = map.get("id");
        this.task_number = task_number;
    }

}
