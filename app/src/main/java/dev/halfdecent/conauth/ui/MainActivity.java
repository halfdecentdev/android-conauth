package dev.halfdecent.conauth.ui;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.Display;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import dev.halfdecent.conauth.R;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private SensorManager mSensorManager;
    private LineData data;

    @BindView(R.id.line_chart) LineChart lineChart;
    @BindView(R.id.record_button) FloatingActionButton recordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        setUpViews();
    }



    private void setUpViews() {
        setUpLineChart();
        recordButton.setOnClickListener(v -> {
            recordButton.hide();
            clearLineChart();
            startRecording();
            new Handler().postDelayed(() -> {
                stopRecording();
                recordButton.show();
            }, 5000);
        });
    }

    private void setUpLineChart() {
        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(createDataSet("X"));
        dataSets.add(createDataSet("Y"));
        dataSets.add(createDataSet("Z"));

        data = new LineData(dataSets);
        lineChart.setDrawGridBackground(false);
        lineChart.setData(data);
        lineChart.invalidate();

        lineChart.getAxisLeft().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getXAxis().setEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setTextColor(ContextCompat.getColor(this, R.color.nord4));
    }

    private LineDataSet createDataSet(String label) {
        LineDataSet dataSet = new LineDataSet(null, label);
        switch (label) {
            case "X":
                dataSet.setColor(ContextCompat.getColor(this, R.color.nord12));
                break;
            case "Y":
                dataSet.setColor(ContextCompat.getColor(this, R.color.nord13));
                break;
            case "Z":
                dataSet.setColor(ContextCompat.getColor(this, R.color.nord14));
                break;
        }
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setDrawCircles(false);
        dataSet.setDrawHighlightIndicators(false);
        dataSet.setDrawValues(false);
        dataSet.setLineWidth(2.5f);
        return dataSet;
    }

    private void clearLineChart() {
        data.getDataSetByIndex(0).clear();
        data.getDataSetByIndex(0).clear();
        data.getDataSetByIndex(0).clear();
        lineChart.invalidate();
    }

    // Accelerometer

    private void startRecording() {
        Sensor accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void stopRecording() {
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                data.addEntry(new Entry(data.getDataSetByIndex(0).getEntryCount(), event.values[0]), 0);
                data.addEntry(new Entry(data.getDataSetByIndex(1).getEntryCount(), event.values[1]), 1);
                data.addEntry(new Entry(data.getDataSetByIndex(2).getEntryCount(), event.values[2]), 2);
                data.notifyDataChanged();
                lineChart.notifyDataSetChanged();
                lineChart.invalidate();
                Log.e(TAG, Arrays.toString(event.values));
                break;
        }
    }
}
