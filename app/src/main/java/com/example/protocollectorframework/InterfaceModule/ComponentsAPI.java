package com.example.protocollectorframework.InterfaceModule;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.protocollectorframework.DataModule.Data.ComponentBuildInfo;
import com.example.protocollectorframework.DataModule.Data.ComponentData;
import com.example.protocollectorframework.DataModule.Data.ComponentView;
import com.example.protocollectorframework.DataModule.Data.EditTextInputFilter;
import com.example.protocollectorframework.Extra.SharedMethods;
import com.example.protocollectorframework.InterfaceModule.CustomViews.CustomDatePicker;
import com.example.protocollectorframework.InterfaceModule.CustomViews.CustomIntervalPicker;
import com.example.protocollectorframework.InterfaceModule.CustomViews.CustomTimePicker;
import com.example.protocollectorframework.InterfaceModule.CustomViews.CustomToggleView;
import com.example.protocollectorframework.MultimediaModule.MultimediaManager;
import com.example.protocollectorframework.R;
import com.google.gson.Gson;
import com.shawnlin.numberpicker.NumberPicker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Class that allows the generation of interface components based on different data types
 */
public class ComponentsAPI {

    public static final int COMPONENT_BOOLEAN = 0;
    public static final int COMPONENT_NUMBER = 1;
    public static final int COMPONENT_TEXT = 2;
    public static final int COMPONENT_TIME = 3;
    public static final int COMPONENT_CATEGORY = 4;
    public static final int COMPONENT_COUNT = 5;
    public static final int COMPONENT_INTERVAL = 6;

    public static final String TYPE_DATE  = "date";
    public static final String TYPE_TIME = "datetime";

    public static final String TYPE_REAL = "real";
    public static final String TYPE_INTEGER = "integer";

    private Context context;
    private static boolean saveValues;
    private Handler incomingHandler;


    /**
     * Default constructor
     * @param context: current activity context
     */
    public ComponentsAPI(Context context){
        this.context = context;
        saveValues = true;
    }

    /**
     * Constructor with activity's handler
     * @param context: current activity context
     * @param incomingHandler: activity handler to handle the modification of the data fields
     */
    public ComponentsAPI(Context context, Handler incomingHandler){
        this.context = context;
        saveValues = true;
        this.incomingHandler = incomingHandler;
    }

    /**
     * Enables or disables the sending of the data to the activity's handler
     * @param save: send the state
     */
    public void setSaveValues(boolean save){
        saveValues = save;
    }

    /**
     * Checks if the sending of the data to the activity's handler is enabled
     * @return if the sending of the data to the activity's handler is enabled
     */
    public boolean getSaveValues(){
        return saveValues;
    }

    /**
     * Sends a message to the current handler with the data associated to the field
     * @param title: title of the field
     * @param value: value of the field
     * @param observation_name: name of the observation
     * @param protocol_name: name of the protocol
     * @param component_type: type of the component
     */
    public void sendStateToActivity(String title, String value, String observation_name, String protocol_name, int component_type){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("title", title);
            jsonObject.put("value", value);
            jsonObject.put("observation_name", observation_name);
            jsonObject.put("protocol_name", protocol_name);
            jsonObject.put("type", component_type);

            Activity activity = (Activity) context;
            Message message1 = new Message();
            message1.what = 1;
            message1.obj = jsonObject;

            if(incomingHandler != null)
                incomingHandler.sendMessage(message1);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Returns the data associated to the field's view
     * @param componentView: field's view
     * @return data associated to the field's view
     */
    public ComponentData getComponent(ComponentView componentView){
        int type = componentView.getType();
        String title = ((TextView)(componentView.getView().findViewById(R.id.component_title))).getText().toString();
        switch (type){
            case COMPONENT_BOOLEAN :
                CustomToggleView toggle =  componentView.getView().findViewById(R.id.component_boolean_toggle);
                String checked = Boolean.toString(toggle.isChecked());
                return new ComponentData(type,title,checked);

            case COMPONENT_NUMBER :
                EditText et = componentView.getView().findViewById(R.id.component_numeric_input);
                String value = "0";
                if(!et.getText().toString().isEmpty())
                    value = et.getText().toString();
                return new ComponentData(type,title,value, componentView.getUnits());
            case COMPONENT_TEXT :
                EditText textEt = componentView.getView().findViewById(R.id.component_text_input);
                String text = textEt.getText().toString();
                return new ComponentData(type,title,text);
            case COMPONENT_TIME :
                CustomDatePicker dp = componentView.getView().findViewById(R.id.date_picker);
                CustomTimePicker tp = componentView.getView().findViewById(R.id.time_picker);
                try {
                    if (dp.getVisibility() == View.VISIBLE) {
                        String date = dp.getDate();
                        return new ComponentData(type,title,date);

                    } else {
                        String time = tp.getTime();
                        return new ComponentData(type,title,time);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case COMPONENT_COUNT :
                NumberPicker np = componentView.getView().findViewById(R.id.component_count_picker);
                String np_value = np.getDisplayedValues()[np.getValue()-1];
                return new ComponentData(type,title,np_value,componentView.getUnits());
            case COMPONENT_INTERVAL :
                CustomIntervalPicker intervalPicker = componentView.getView().findViewById(R.id.component_interval_picker);
                String intervalValue = intervalPicker.getProcessedValue();
                return new ComponentData(type,title,intervalValue);
            case COMPONENT_CATEGORY :
                GridView gridView = componentView.getView().findViewById(R.id.categories_grid_view);
                String category_value = "";
                return new ComponentData(type,title,category_value);
        }

        return null;
    }

    /**
     * Sets the value on the view based on the given data
     * @param cd: field's data
     * @param view: field's view
     */
    public void setComponentValue(ComponentData cd, ComponentView view){
        String value = cd.getValue();
        switch (cd.getType()){
            case COMPONENT_BOOLEAN :
                CustomToggleView toggle = view.getView().findViewById(R.id.component_boolean_toggle);
                boolean check = Boolean.parseBoolean(value);
                toggle.setChecked(check);
                break;
            case COMPONENT_NUMBER :
                EditText et = view.getView().findViewById(R.id.component_numeric_input);
                if(!value.equals(et.getHint().toString()))
                    et.setText(value);
                else{
                    et.setText("");
                }
                break;
            case COMPONENT_TEXT :
                EditText textEt = view.getView().findViewById(R.id.component_text_input);
                textEt.setText(value);
                break;
            case COMPONENT_TIME :
                CustomDatePicker dp = view.getView().findViewById(R.id.date_picker);
                CustomTimePicker tp = view.getView().findViewById(R.id.time_picker);
                try {
                    if (dp.getVisibility() == View.VISIBLE) {
                        String[] date = value.split("/");
                        dp.setDate(Integer.parseInt(date[2]), Integer.parseInt(date[1]), Integer.parseInt(date[0]));
                    } else {
                        String[] time = value.split(":");
                        tp.setTime(Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case COMPONENT_COUNT :
                NumberPicker np = view.getView().findViewById(R.id.component_count_picker);
                List<String> aux = Arrays.asList(np.getDisplayedValues());
                np.setValue(aux.indexOf(value) + 1);
                break;
            case COMPONENT_INTERVAL :
                CustomIntervalPicker intervalPicker = view.getView().findViewById(R.id.component_interval_picker);
                String[] rawSplitValue = value.split("/");
                if(rawSplitValue.length == 2)
                    intervalPicker.setValues(rawSplitValue[0],rawSplitValue[1]);
                else
                    intervalPicker.setValues(rawSplitValue[0],rawSplitValue[0]);

                break;
            case COMPONENT_CATEGORY :
                List<String> category_values = new ArrayList<>();
                if(value != null && !value.isEmpty()) {
                    try {
                        JSONArray jsonArray = new JSONArray(value);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            category_values.add(jsonArray.getString(i));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                GridView gridView = view.getView().findViewById(R.id.categories_grid_view);
                CategoriesAdapter adapter = (CategoriesAdapter) gridView.getAdapter();
                gridView.setAdapter(new CategoriesAdapter(context, adapter.getCategories(),category_values));
                break;

        }
    }

    /**
     * Creates the field view based on build info extracted from the protocol specification
     * @param buildInfo: component build info extracted from the protocol
     * @return field's view
     */
    public ComponentView setComponent(ComponentBuildInfo buildInfo){
        int type = buildInfo.getType();
        String name = buildInfo.getLabel();
        String value_type = buildInfo.getValue_type();
        int min = buildInfo.getMin();
        int max = buildInfo.getMax();
        String[] values = buildInfo.getCountValues();
        String temporal_type = buildInfo.getTemporalType();
        String units = buildInfo.getUnits();
        String observation_name = buildInfo.getObservation_name();
        String protocol_name = buildInfo.getProtocol();
        String[] firstValues = buildInfo.getFirst_values();
        String[] lastValues = buildInfo.getLast_values();
        boolean unique = buildInfo.getUnique();

        switch (type){
            case COMPONENT_BOOLEAN :
                return setComponentBoolean(name,observation_name,protocol_name);
            case COMPONENT_NUMBER :
                return setComponentNumeric(name,value_type,min,max,units,observation_name,protocol_name);
            case COMPONENT_TEXT :
                return setComponentText(name,observation_name,protocol_name);
            case COMPONENT_TIME :
                return setComponentTemporal(name,temporal_type,observation_name,protocol_name);
            case COMPONENT_COUNT :
                return setComponentCount(name,values,units,observation_name,protocol_name);
            case COMPONENT_INTERVAL :
                return setComponentInterval(name,firstValues,lastValues,observation_name,protocol_name);
            case COMPONENT_CATEGORY :
                return setComponentCategory(name,observation_name,protocol_name,values,unique);

        }
        return null;
    }


    /**
     * Generates a boolean component
     * @param title: field's title
     * @param observation_name: observation's name
     * @param protocol_name: protocol's name
     * @return boolean component view
     */
    private ComponentView setComponentBoolean(String title, String observation_name, String protocol_name){
        ComponentView v = new ComponentView(COMPONENT_BOOLEAN, View.inflate(context, R.layout.component_boolean_layout, null));
        TextView textView = v.getView().findViewById(R.id.component_title);
        textView.setText(title);

        CustomToggleView tg = v.getView().findViewById(R.id.component_boolean_toggle);

        tg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getSaveValues()) {
                    sendStateToActivity(title, Boolean.toString(tg.isChecked()),observation_name,protocol_name,COMPONENT_BOOLEAN);
                }
            }
        });

        tg.setChecked(false);
        return v;
    }

    /**
     * Generates a interval component
     * @param title: field's title
     * @param firstValues: left domain of the interval
     * @param lastValues: right domain of the interval
     * @param observation_name: observation's name
     * @param protocol_name: protocol's name
     * @return interval component view
     */
    private ComponentView setComponentInterval(String title, String[] firstValues, String[] lastValues, String observation_name, String protocol_name){
        ComponentView v = new ComponentView(COMPONENT_INTERVAL, View.inflate(context, R.layout.component_interval_layout, null));

        CustomIntervalPicker intervalPicker = v.getView().findViewById(R.id.component_interval_picker);
        intervalPicker.setValues(firstValues,lastValues);
        intervalPicker.setVisibility(View.VISIBLE);

        TextView textView = v.getView().findViewById(R.id.component_title);
        textView.setText(title);


        intervalPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getSaveValues()) {
                    sendStateToActivity(title,intervalPicker.getProcessedValue(),observation_name,protocol_name,COMPONENT_BOOLEAN);
                }
            }
        });

        return v;
    }


    /**
     * Generates a count component
     * @param title: field's title
     * @param values: domain of allowed values
     * @param units: units of the count
     * @param observation_name: observation's name
     * @param protocol_name: protocol's name
     * @return count component view
     */
    public ComponentView setComponentCount(String title, String[] values, String units, String observation_name, String protocol_name){
        ComponentView v = new ComponentView(COMPONENT_COUNT, View.inflate(context, R.layout.component_count_layout, null),units);
        TextView textView = v.getView().findViewById(R.id.component_title);
        textView.setText(title);

        NumberPicker numberPicker = v.getView().findViewById(R.id.component_count_picker);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(values.length);
        numberPicker.setDisplayedValues(values);
        numberPicker.setValue(1);
        numberPicker.setWrapSelectorWheel(false);

        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if(getSaveValues()) {
                    sendStateToActivity(title,values[newVal-1],observation_name,protocol_name,COMPONENT_COUNT);
                }
            }
        });

        return v;

    }

    /**
     * Generates a numeric component
     * @param title: field's title
     * @param value_type: type of value (integer or real)
     * @param min: minimum  allowed value
     * @param max: maximum allowed value
     * @param units: units associated
     * @param observation_name: observation's name
     * @param protocol_name: protocol's name
     * @return numeric component view
     */
    public ComponentView setComponentNumeric(String title, String value_type, int min, int max, String units, String observation_name, String protocol_name) {
        ComponentView v;
        EditText et;

        if(min >= max){
            min = Integer.MIN_VALUE;
            max = Integer.MAX_VALUE;
        }

        if(max != Integer.MAX_VALUE) {
            v = new ComponentView(COMPONENT_NUMBER, View.inflate(context, R.layout.component_numeric_layout_with_max, null),units);

            TextView max_hint = v.getView().findViewById(R.id.max_hint);
            TextView separator = v.getView().findViewById(R.id.separator);
            max_hint.setText(Integer.toString(max));
            max_hint.setVisibility(View.VISIBLE);
            separator.setVisibility(View.VISIBLE);

            et = v.getView().findViewById(R.id.component_numeric_input);
            ConstraintLayout cl = v.getView().findViewById(R.id.edit_text_with_max);
            cl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    et.setFocusableInTouchMode(true);
                    et.requestFocus();
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(et, InputMethodManager.SHOW_FORCED);
                }
            });

        }else{
            v = new ComponentView(COMPONENT_NUMBER, View.inflate(context, R.layout.component_numeric_layout, null),units);
            et = v.getView().findViewById(R.id.component_numeric_input);
        }

        et.setFilters(new InputFilter[]{ new EditTextInputFilter(min, max),new InputFilter.LengthFilter(8)});

        if(value_type.equals(TYPE_REAL))
            et.setInputType(min < 0 || max < 0 ? InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED: InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        else{
            et.setInputType(min < 0 || max < 0 ? InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED : InputType.TYPE_CLASS_NUMBER);
        }



        et.addTextChangedListener(new TextWatcher() {
            private String oldText;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                oldText = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(getSaveValues()) {
                    try {
                        String value;
                        if(s.toString().isEmpty() || s.toString().equals("-"))
                            value = et.getHint().toString();
                        else value = s.toString();

                        sendStateToActivity(title,value,observation_name,protocol_name,COMPONENT_NUMBER);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(oldText.equals("0") && !s.toString().isEmpty()) {
                    et.setText(s.toString().substring(1));
                    et.setSelection(1);
                }
            }
        });

        TextView textView = v.getView().findViewById(R.id.component_title);
        textView.setText(title);
        return v;

    }

    /**
     * Generates a textual component
     * @param title: field's title
     * @param observation_name: observation's name
     * @param protocol_name: protocol's name
     * @return textual component view
     */

    @SuppressLint("ClickableViewAccessibility")
    public ComponentView setComponentText(String title, String observation_name, String protocol_name) {
        ComponentView v = new ComponentView(COMPONENT_TEXT, View.inflate(context, R.layout.component_titled_textual_layout, null));

        EditText et = v.getView().findViewById(R.id.component_text_input);



        et.addTextChangedListener(new TextWatcher() {
            private String oldText;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                oldText = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(getSaveValues()) {
                    try {
                        String value = s.toString();
                        sendStateToActivity(title,value,observation_name,protocol_name,COMPONENT_TEXT);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        Activity aux = null;
        try{
            aux = (Activity) context;
        }catch (Exception e){
            e.printStackTrace();
        }
        final Activity activity = aux;
        et.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (et.getRight() - et.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if(activity != null)
                            new MultimediaManager(context).requestVoiceInput(activity, et);
                        else
                            SharedMethods.showToast(context,context.getResources().getString(R.string.prompt_updates_failed));
                        return true;
                    }
                }
                return false;
            }
        });

        TextView textView = v.getView().findViewById(R.id.component_title);
        textView.setText(title);
        return v;

    }


    /**
     * Generates a temporal component
     * @param title: field's title
     * @param type: type of temporal data (date or datetime)
     * @param observation_name: observation's name
     * @param protocol_name: protocol's name
     * @return temporal component view
     */
    public ComponentView setComponentTemporal(String title, String type, String observation_name, String protocol_name){
        ComponentView v = null;
        try {
            v = new ComponentView(COMPONENT_TIME, View.inflate(context, R.layout.component_temporal_layout, null));
        }catch (Exception e){
            e.printStackTrace();
        }
        TextView textView = v.getView().findViewById(R.id.component_title);
        textView.setText(title);
        switch (type){
            case TYPE_DATE:
                CustomDatePicker datePicker = v.getView().findViewById(R.id.date_picker);
                datePicker.setVisibility(View.VISIBLE);
                datePicker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(getSaveValues()) {
                            sendStateToActivity(title,datePicker.getDate(),observation_name,protocol_name,COMPONENT_TIME);
                        }
                    }
                });

                break;
            case TYPE_TIME:
                CustomTimePicker timePicker = v.getView().findViewById(R.id.time_picker);
                timePicker.setVisibility(View.VISIBLE);
                timePicker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(getSaveValues()) {
                            sendStateToActivity(title,timePicker.getTime(),observation_name,protocol_name,COMPONENT_TIME);
                        }
                    }
                });

                break;
            default: v.getView().setVisibility(View.GONE); break;
        }
        return v;
    }

    /**
     * Generates a categorical component
     * @param title: field's title
     * @param observation_name: observation's name
     * @param protocol_name: protocol's name
     * @param categories: possible categories
     * @param unique: uniqueness of the selection
     * @return categorical component view
     */
    private ComponentView setComponentCategory(String title, String observation_name, String protocol_name, String[] categories, boolean unique){
        ComponentView v = new ComponentView(COMPONENT_CATEGORY, View.inflate(context, R.layout.component_categorical_layout, null));
        TextView textView = v.getView().findViewById(R.id.component_title);
        textView.setText(title);

        GridView gv = v.getView().findViewById(R.id.categories_grid_view);

        if(categories.length == 2)
            gv.setNumColumns(2);

        gv.setAdapter(new CategoriesAdapter(context, categories,null));

        List<String> selected = new ArrayList<>(categories.length);


        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                TextView textView = ((TextView) v.findViewById(R.id.text_category));
                String value = textView.getText().toString();


                if (Objects.equals(textView.getBackground().getConstantState(), context.getResources().getDrawable(R.drawable.pob_square).getConstantState())) {
                    if (unique) {
                        for (int i = 0; i < parent.getChildCount(); i++) {
                            TextView old = parent.getChildAt(i).findViewById(R.id.text_category);
                            old.setBackground(context.getResources().getDrawable(R.drawable.pob_square));
                            old.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                        }
                        if(selected.size() > 0)
                            selected.remove(0);
                    }
                    selected.add(value);
                    String json = new Gson().toJson(selected);

                    sendStateToActivity(title,json,observation_name,protocol_name,COMPONENT_CATEGORY);
                    textView.setBackground(context.getResources().getDrawable(R.drawable.pob_square_selected));
                    textView.setTextColor(Color.WHITE);
                }else {
                    if(selected.size() > 0)
                        selected.remove(value);
                    String json = new Gson().toJson(selected);
                    sendStateToActivity(title,json,observation_name,protocol_name,COMPONENT_CATEGORY);
                    textView.setBackground(context.getResources().getDrawable(R.drawable.pob_square));
                    textView.setTextColor(context.getResources().getColor(R.color.colorPrimary));

                }
            }
        });


        return v;
    }

}
