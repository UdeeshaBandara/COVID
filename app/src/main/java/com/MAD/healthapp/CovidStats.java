package com.MAD.healthapp;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CovidStats extends Fragment {
    private static final String TAG = "Stats";

    private PieChart pieChart;
    private BarChart barChart;
    private LineChart lineChart;
    String topic[] = {"Active", "Recovered", "Dead"};
    int values[] = new int[3];
    AutoCompleteTextView txt_countries;
    String countries[][] = new String[2][248];
    ArrayList<BarEntry> lastTenDailyCases = new ArrayList<>();
    ArrayList<Entry> lastTenDailyDeaths = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.covid_stats, container, false);


        pieChart = v.findViewById(R.id.SumChart);
        barChart = v.findViewById(R.id.dailyBarChart);
        lineChart = v.findViewById(R.id.lineChart);
        txt_countries = v.findViewById(R.id.txt_countries);
        pieChart.getDescription().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        lineChart.getDescription().setEnabled(false);
//        lineChart.setOnChartGestureListener((OnChartGestureListener) v.getContext());
        //      lineChart.setOnChartValueSelectedListener((OnChartValueSelectedListener) v.getContext());
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);

        final SweetAlertDialog pDialog = new SweetAlertDialog(v.getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Please Wait");

        pDialog.setCancelable(false);

        pDialog.show();
        getSum("Sri Lanka");
        getDailyUpdate("sri-lanka");
        // String api2="https://covid-api.com/api/regions";
        String api1 = "https://api.covid19api.com/countries";
        RequestQueue queue = Volley.newRequestQueue(v.getContext());
        StringRequest request = new StringRequest(Request.Method.GET, api1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    // for 1st API
                    JSONArray covidStats = new JSONArray(response);

                    for (int i = 0; i < covidStats.length(); i++) {

                        countries[0][i] = covidStats.getJSONObject(i).getString("Country");
                        countries[1][i] = covidStats.getJSONObject(i).getString("Slug");


                    }

                    /*for 2nd API
                    JSONObject covidStats = new JSONObject(response);
                    JSONArray ar=covidStats.getJSONArray("data");
                    Log.e("array",String.valueOf(covidStats.getJSONArray("data").getJSONObject(1).getString("name")));
                    for (int i = 0; i < covidStats.getJSONArray("data").length(); i++){

                        countries[0][i] =ar.getJSONObject(i).getString("name");
                    }*/
                    ArrayAdapter<String> Adaptor = new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_list_item_1, countries[0]);
                    Adaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    txt_countries.setAdapter(Adaptor);


                } catch (Exception ex) {

                    Log.e("Response", ex.getMessage());
                }
                pDialog.dismissWithAnimation();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismissWithAnimation();

            }
        });

        queue.add(request);

        txt_countries.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                getSum(String.valueOf(parent.getItemAtPosition(position)));
                for (int d = 0; d < countries[1].length; d++) {
                    if (String.valueOf(parent.getItemAtPosition(position)).equals(countries[0][d])) {
                        getDailyUpdate(countries[1][d]);

                    }

                }
            }
        });

        return v;
    }


    private void addLineDataSet() {
        LineDataSet rec = new LineDataSet(lastTenDailyDeaths, "Deaths");
        rec.setFillAlpha(110);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(rec);
        LineData data = new LineData(dataSets);
        lineChart.setData(data);

    }

    private void getDailyUpdate(final String s) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String api_1st = "https://api.covid19api.com/total/country/" + s + "";

        StringRequest requestSum = new StringRequest(Request.Method.GET, api_1st, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray covidDailyCases = new JSONArray();
                    covidDailyCases = new JSONArray(response);

                    try {
                        lastTenDailyCases.clear();
                        lastTenDailyCases.add(new BarEntry(0, (Integer.parseInt(covidDailyCases.getJSONObject(covidDailyCases.length() - 10).getString("Confirmed"))) - Integer.parseInt(covidDailyCases.getJSONObject(covidDailyCases.length() - 11).getString("Confirmed"))));
                        lastTenDailyCases.add(new BarEntry(1, (Integer.parseInt(covidDailyCases.getJSONObject(covidDailyCases.length() - 9).getString("Confirmed"))) - Integer.parseInt(covidDailyCases.getJSONObject(covidDailyCases.length() - 10).getString("Confirmed"))));
                        lastTenDailyCases.add(new BarEntry(2, (Integer.parseInt(covidDailyCases.getJSONObject(covidDailyCases.length() - 8).getString("Confirmed"))) - Integer.parseInt(covidDailyCases.getJSONObject(covidDailyCases.length() - 9).getString("Confirmed"))));
                        lastTenDailyCases.add(new BarEntry(3, (Integer.parseInt(covidDailyCases.getJSONObject(covidDailyCases.length() - 7).getString("Confirmed"))) - Integer.parseInt(covidDailyCases.getJSONObject(covidDailyCases.length() - 8).getString("Confirmed"))));
                        lastTenDailyCases.add(new BarEntry(4, (Integer.parseInt(covidDailyCases.getJSONObject(covidDailyCases.length() - 6).getString("Confirmed"))) - Integer.parseInt(covidDailyCases.getJSONObject(covidDailyCases.length() - 7).getString("Confirmed"))));
                        lastTenDailyCases.add(new BarEntry(5, (Integer.parseInt(covidDailyCases.getJSONObject(covidDailyCases.length() - 5).getString("Confirmed"))) - Integer.parseInt(covidDailyCases.getJSONObject(covidDailyCases.length() - 6).getString("Confirmed"))));
                        lastTenDailyCases.add(new BarEntry(6, (Integer.parseInt(covidDailyCases.getJSONObject(covidDailyCases.length() - 4).getString("Confirmed"))) - Integer.parseInt(covidDailyCases.getJSONObject(covidDailyCases.length() - 5).getString("Confirmed"))));
                        lastTenDailyCases.add(new BarEntry(7, (Integer.parseInt(covidDailyCases.getJSONObject(covidDailyCases.length() - 3).getString("Confirmed"))) - Integer.parseInt(covidDailyCases.getJSONObject(covidDailyCases.length() - 4).getString("Confirmed"))));
                        lastTenDailyCases.add(new BarEntry(8, (Integer.parseInt(covidDailyCases.getJSONObject(covidDailyCases.length() - 2).getString("Confirmed"))) - Integer.parseInt(covidDailyCases.getJSONObject(covidDailyCases.length() - 3).getString("Confirmed"))));
                        lastTenDailyCases.add(new BarEntry(9, (Integer.parseInt(covidDailyCases.getJSONObject(covidDailyCases.length() - 1).getString("Confirmed"))) - Integer.parseInt(covidDailyCases.getJSONObject(covidDailyCases.length() - 2).getString("Confirmed"))));
                        lastTenDailyDeaths.clear();
                        lastTenDailyDeaths.add(new Entry(0, (Integer.parseInt(covidDailyCases.getJSONObject(covidDailyCases.length() - 10).getString("Deaths"))) - Integer.parseInt(covidDailyCases.getJSONObject(covidDailyCases.length() - 11).getString("Deaths"))));
                        lastTenDailyDeaths.add(new Entry(1, (Integer.parseInt(covidDailyCases.getJSONObject(covidDailyCases.length() - 9).getString("Deaths"))) - Integer.parseInt(covidDailyCases.getJSONObject(covidDailyCases.length() - 10).getString("Deaths"))));
                        lastTenDailyDeaths.add(new Entry(2, (Integer.parseInt(covidDailyCases.getJSONObject(covidDailyCases.length() - 8).getString("Deaths"))) - Integer.parseInt(covidDailyCases.getJSONObject(covidDailyCases.length() - 9).getString("Deaths"))));
                        lastTenDailyDeaths.add(new Entry(3, (Integer.parseInt(covidDailyCases.getJSONObject(covidDailyCases.length() - 7).getString("Deaths"))) - Integer.parseInt(covidDailyCases.getJSONObject(covidDailyCases.length() - 8).getString("Deaths"))));
                        lastTenDailyDeaths.add(new Entry(4, (Integer.parseInt(covidDailyCases.getJSONObject(covidDailyCases.length() - 6).getString("Deaths"))) - Integer.parseInt(covidDailyCases.getJSONObject(covidDailyCases.length() - 7).getString("Deaths"))));
                        lastTenDailyDeaths.add(new Entry(5, (Integer.parseInt(covidDailyCases.getJSONObject(covidDailyCases.length() - 5).getString("Deaths"))) - Integer.parseInt(covidDailyCases.getJSONObject(covidDailyCases.length() - 6).getString("Deaths"))));
                        lastTenDailyDeaths.add(new Entry(6, (Integer.parseInt(covidDailyCases.getJSONObject(covidDailyCases.length() - 4).getString("Deaths"))) - Integer.parseInt(covidDailyCases.getJSONObject(covidDailyCases.length() - 5).getString("Deaths"))));
                        lastTenDailyDeaths.add(new Entry(7, (Integer.parseInt(covidDailyCases.getJSONObject(covidDailyCases.length() - 3).getString("Deaths"))) - Integer.parseInt(covidDailyCases.getJSONObject(covidDailyCases.length() - 4).getString("Deaths"))));
                        lastTenDailyDeaths.add(new Entry(8, (Integer.parseInt(covidDailyCases.getJSONObject(covidDailyCases.length() - 2).getString("Deaths"))) - Integer.parseInt(covidDailyCases.getJSONObject(covidDailyCases.length() - 3).getString("Deaths"))));
                        lastTenDailyDeaths.add(new Entry(9, (Integer.parseInt(covidDailyCases.getJSONObject(covidDailyCases.length() - 1).getString("Deaths"))) - Integer.parseInt(covidDailyCases.getJSONObject(covidDailyCases.length() - 2).getString("Deaths"))));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    addBarDataSet();
                    addLineDataSet();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(requestSum);
    }

    private void addBarDataSet() {
        barChart.setFitBars(true);
        BarDataSet barChartData = new BarDataSet(lastTenDailyCases, "Dates");
        barChartData.setColors(ColorTemplate.MATERIAL_COLORS);
        barChartData.setDrawValues(true);
        BarData data = new BarData(barChartData);
        barChart.setData(data);
        barChart.invalidate();
        barChart.animateY(500);
    }

    private void getSum(final String country) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String api_1st = "https://api.covid19api.com/summary";
        String api_2nd = "https://covid-api.com/api/reports?region_name=" + country + "";
        StringRequest requestSum = new StringRequest(Request.Method.GET, api_1st, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    //for 1st API
                    JSONObject covidStats1 = new JSONObject(response);
                    JSONArray countryArray1 = covidStats1.getJSONArray("Countries");
                    for (int c = 0; c < countryArray1.length(); c++) {
                        if (country.equals(countryArray1.getJSONObject(c).getString("Country"))) {

                            values[2] = Integer.parseInt(countryArray1.getJSONObject(c).getString("TotalDeaths"));
                            values[1] = Integer.parseInt(countryArray1.getJSONObject(c).getString("TotalRecovered"));
                            values[0] = Integer.parseInt(countryArray1.getJSONObject(c).getString("TotalConfirmed")) - (values[1] + values[2]);
                            break;
                        }

                    }
                  /*for 2nd API
                    JSONObject covidStats1 = new JSONObject(response);
                    values[2]=Integer.parseInt(covidStats1.getJSONArray("data").getJSONObject(0).getString("deaths"));
                   values[1]= Integer.parseInt(covidStats1.getJSONArray("data").getJSONObject(0).getString("recovered"));
                   values[0]= Integer.parseInt(covidStats1.getJSONArray("data").getJSONObject(0).getString("confirmed"))-(values[1]+values[2]);
*/

                    chart(country);
                } catch (Exception ex) {

                    Log.e("Response", ex.getMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(requestSum);
    }

    private void chart(String country) {
        // pieChart.setRotationEnabled(true);
        //pieChart.setUsePercentValues(true);
        //pieChart.setHoleColor(Color.BLUE);
        //pieChart.setCenterTextColor(Color.BLACK);
        pieChart.setHoleRadius(25f);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setCenterText(country + "\nCOVID 19 Stats");
        pieChart.setCenterTextSize(15);
        //pieChart.setDrawEntryLabels(true);
        //pieChart.setEntryLabelTextSize(20);
        //More options just check out the documentation!

        addPieDataSet();

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {


                int pos1 = e.toString().indexOf("(sum): ");
                String sales = e.toString().substring(pos1 + 7);

                for (int i = 0; i < values.length; i++) {
                    if (values[i] == Float.parseFloat(sales.substring(11))) {
                        pos1 = i;
                        break;
                    }
                }

                Toast.makeText(getContext(), "Total no of " + topic[pos1] + "\n" + "peoples: " + sales.substring(9), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });


    }


    private void addPieDataSet() {

        ArrayList<PieEntry> yEntrys = new ArrayList<>();
        ArrayList<String> xEntrys = new ArrayList<>();

        for (int i = 0; i < values.length; i++) {
            yEntrys.add(new PieEntry(values[i], i));
        }

        for (int i = 1; i < topic.length; i++) {
            xEntrys.add(topic[i]);
        }

        //create the data set
        PieDataSet pieDataSet = new PieDataSet(yEntrys, "COVID Stats");
        pieDataSet.setSliceSpace(5);
        pieDataSet.setValueTextSize(15);

        //add colors to dataset
        ArrayList<Integer> colors = new ArrayList<>();

        colors.add(Color.BLUE);

        colors.add(Color.GREEN);
        colors.add(Color.RED);

        pieDataSet.setColors(colors);

        //add legend to chart
        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        //hn legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);

        //create pie data object
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }
}
