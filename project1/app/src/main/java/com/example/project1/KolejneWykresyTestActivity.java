package com.example.project1;

import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class KolejneWykresyTestActivity extends AppCompatActivity {


    LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kolejne_wykresy_test);

        lineChart = (LineChart) findViewById(R.id.wykresWagiOdCzasu);
        lineChart.setTouchEnabled(false);
        lineChart.setPinchZoom(false);

        ArrayList<Entry> dataVal = new ArrayList<>();
        dataVal.add(new Entry(0,65));
        dataVal.add(new Entry(1,66));
        dataVal.add(new Entry(2,67));
        dataVal.add(new Entry(3,68));
        dataVal.add(new Entry(4,65));

        LineDataSet lineDataSet = new LineDataSet(dataVal, "Zmiany wagi w czasie");
        int lineColor = Color.rgb(52,235,55);
        int dotsColor = Color.rgb(235, 52, 79);

        lineDataSet.setColors(new int[]{lineColor});
        lineDataSet.setValueTextSize(13f);
        lineDataSet.setCircleColor(dotsColor);
        lineDataSet.setLineWidth(4f);
       // lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);
        LineData data = new LineData(dataSets);
        lineChart.setData(data);
        lineChart.invalidate();



        String[] strings = {"26-02-2023","26-02-2023","27-02-2023", "28-02-2023", "29-02-2023", "30-02-2023"};
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(11f);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(strings));



        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setTextSize(15f);
        yAxis.setGranularity(1f);


        Legend legend = lineChart.getLegend();
        legend.setEnabled(true);
        legend.setTextSize(15f);


        lineChart.setAutoScaleMinMaxEnabled(true);
    }
}