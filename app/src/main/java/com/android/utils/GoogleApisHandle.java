package com.android.utils;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class GoogleApisHandle {
    private static final GoogleApisHandle mapUtils = new GoogleApisHandle();
    private static DistanceCalculated onDistanceCalculated;
    private GoogleMap routeMap;
    private Context context;
    private LatLng origin, destination;

    private OnPolyLineReceived onPolyLineReceived;
    private RoateMarkerRunnable roateMarkerRunnable;
    private AnimateMarkerRunnable animateMarkerRunnable;
    private Handler handler = new Handler();

    public static GoogleApisHandle with(Context context) {
        mapUtils.setAct(context);
        return mapUtils;
    }

    private void setAct(Context mAct) {
        this.context = mAct;
    }

    public String decodeAddressFromLatLng(double lat, double lang) {
        try {
            Geocoder geocoder;
            String fullAddress = "Not Found";
            List<Address> addresses;
            geocoder = new Geocoder(context);
            if (lat != 0 || lang != 0) {
                addresses = geocoder.getFromLocation(lat, lang, 1);
                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getAddressLine(1);
                String country = addresses.get(0).getAddressLine(2);
                String state = addresses.get(0).getSubLocality();
                fullAddress = (address != null ? address : "") + (city != null ? ", " + city : "") + (state != null ? ", " + state : "") + (country != null ? ", " + country : "");
                if (fullAddress.equals("")) {
                    JSONObject json = getJSONfromURL("http://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lang + "&sensor=true");
                    try {
                        if (json.getJSONArray("results").length() > 0)
                            return json.getJSONArray("results").getJSONObject(0).getString("formatted_address");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return fullAddress;
            } else {
                return fullAddress;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public LatLng getLatLngFromAddress(String address) {
        List<Address> addresses;
        Geocoder geocoder = new Geocoder(context);
        try {
            List<Address> addressList = geocoder.getFromLocationName(address, 1);
            if (addressList.size() > 0)
                return new LatLng(addressList.get(0).getLatitude(), addressList.get(0).getLongitude());
            else {
                JSONObject object = getJSONfromURL("https://maps.googleapis.com/maps/api/geocode/json?address=" + address.replace(" ", "%20"));
                JSONObject jsonObject = object.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                return new LatLng(jsonObject.getDouble("lat"), jsonObject.getDouble("lng"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new LatLng(0.0, 0.0);
    }

    private JSONObject getJSONfromURL(String url) {

        // initialize
        InputStream is = null;
        String result = "";
        JSONObject jObject = null;


        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();

        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection " + e.toString());
        }

        // convert response to string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
            jObject = new JSONObject(result);
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }

        return jObject;
    }

    public void getDirectionsUrl(LatLng origin, LatLng dest, GoogleMap googleMap) {
        String str_origin = "origin=" + origin.latitude + ","
                + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        new DownloadTask(origin, dest, googleMap).execute(url);
    }

    public String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
        } finally {
            if (iStream != null)
                iStream.close();
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return data;
    }

    private double bearingBetweenLocations(double fromLat, double fromLong, double toLat, double toLong) {
        double PI = 3.14159;
        double lat1 = fromLat * PI / 180;
        double long1 = fromLong * PI / 180;
        double lat2 = toLat * PI / 180;
        double long2 = toLong * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }


    public void animateMarkerToGB(LatLng finalPosition, Marker oldMarker, boolean isCameraAnimate, GoogleMap googleMap) {

        LatLng startPosition = oldMarker.getPosition();
        double lat1 = startPosition.latitude;
        double lng1 = startPosition.longitude;
        double lat2 = finalPosition.latitude;
        double lng2 = finalPosition.longitude;
        if (roateMarkerRunnable != null)
            handler.removeCallbacks(roateMarkerRunnable);
        if (animateMarkerRunnable != null)
            handler.removeCallbacks(animateMarkerRunnable);
        rotateMarker(lat1, lng1, lat2, lng2, oldMarker, handler, isCameraAnimate, googleMap);

    }


    private class AnimateMarkerRunnable implements Runnable {
        Interpolator interpolator;
        Marker oldMarker;
        LatLngInterpolator latLngInterpolator;
        LatLng startPosition;
        LatLng finalPosition;
        boolean isCameraAnimate;
        GoogleMap googleMap;
        Handler handler;
        float durationInMs;
        long start;
        long elapsed;
        float t;
        float v;

        AnimateMarkerRunnable(long start, float durationInMs, Interpolator interpolator, Marker oldMarker, LatLngInterpolator latLngInterpolator, LatLng startPosition, LatLng finalPosition, boolean isCameraAnimate, GoogleMap googleMap, Handler handler) {
            this.start = start;
            this.durationInMs = durationInMs;
            this.interpolator = interpolator;
            this.oldMarker = oldMarker;
            this.latLngInterpolator = latLngInterpolator;
            this.startPosition = startPosition;
            this.finalPosition = finalPosition;
            this.isCameraAnimate = isCameraAnimate;
            this.googleMap = googleMap;
            this.handler = handler;
        }

        @Override
        public void run() {
            elapsed = SystemClock.uptimeMillis() - start;
            t = elapsed / durationInMs;
            v = interpolator.getInterpolation(t);
            oldMarker.setPosition(latLngInterpolator.interpolate(v, startPosition, finalPosition));
            if (isCameraAnimate) {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(startPosition);
                builder.include(finalPosition);
                LatLngBounds bounds = builder.build();
                int padding = 10;
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,
                        padding);
                googleMap.animateCamera(cu);
            }
            //Repeat till progress is complete.
            if (t < 1) {                     // Post again 16ms later.
                handler.postDelayed(animateMarkerRunnable, 16);
            } else {
                handler.removeCallbacks(animateMarkerRunnable);
                animateMarkerRunnable = null;
            }
        }
    }

    public void rotateMarker(double fromLat, double fromLong, double toLat, double toLong, Marker marker, Handler handler, boolean isCameraAnimate, GoogleMap googleMap) {
        double brng = bearingBetweenLocations(fromLat, fromLong, toLat, toLong);
        long start = SystemClock.uptimeMillis();
        float startRotation = marker.getRotation();
        float toRotation = (float) brng;
        long duration = 1000;
        LatLng finalPosition = new LatLng(toLat, toLong);
        Interpolator interpolator = new LinearInterpolator();
        roateMarkerRunnable = new RoateMarkerRunnable(start, interpolator, duration, toRotation, startRotation, marker, handler, finalPosition, isCameraAnimate, googleMap);
        handler.post(roateMarkerRunnable);
    }

    private class RoateMarkerRunnable implements Runnable {

        Interpolator interpolator;
        float duration;
        float toRotation;
        float startRotation;
        Marker marker;
        Handler handler;
        long start;
        LatLng finalPosition;
        boolean isCameraAnimate;
        GoogleMap googleMap;

        public RoateMarkerRunnable(long start, Interpolator interpolator, float duration, float toRotation, float startRotation, Marker marker, Handler handler, LatLng finalPosition, boolean isCameraAnimate, GoogleMap googleMap) {
            this.start = start;
            this.interpolator = interpolator;
            this.duration = duration;
            this.toRotation = toRotation;
            this.startRotation = startRotation;
            this.marker = marker;
            this.handler = handler;
            this.finalPosition = finalPosition;
            this.isCameraAnimate = isCameraAnimate;
            this.googleMap = googleMap;
        }

        @Override
        public void run() {
            long elapsed = SystemClock.uptimeMillis() - start;
            float t = interpolator.getInterpolation((float) elapsed / duration);

            float rot = t * toRotation + (1 - t) * startRotation;

            marker.setRotation(-rot >= 180 ? rot / 2 : rot);
            if (t < 1.0) {
                // Post again 16ms later.
                handler.postDelayed(roateMarkerRunnable, 16);
            } else {
                handler.removeCallbacks(roateMarkerRunnable);
                roateMarkerRunnable = null;
                animateMarker(marker, finalPosition, isCameraAnimate, googleMap);
            }
        }
    }

    private void animateMarker(Marker oldMarker, LatLng finalPosition, boolean isCameraAnimate, GoogleMap googleMap) {
        LatLng startPosition = oldMarker.getPosition();
        LatLngInterpolator latLngInterpolator = new LatLngInterpolator.Spherical();
        long start = SystemClock.uptimeMillis();
        Interpolator interpolator = new AccelerateDecelerateInterpolator();
        float durationInMs = 9 * 1000;
        animateMarkerRunnable = new AnimateMarkerRunnable(start, durationInMs, interpolator, oldMarker, latLngInterpolator, startPosition, finalPosition, isCameraAnimate, googleMap, handler);
        handler.post(animateMarkerRunnable);
    }


    public interface DistanceCalculated {

        void sendDistance(double distance);
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        public DownloadTask(LatLng source, LatLng dest, GoogleMap map) {

            origin = source;
            destination = dest;
            routeMap = map;
        }

        public DownloadTask(LatLng source, LatLng dest, DistanceCalculated distanceCalculated) {

            onDistanceCalculated = distanceCalculated;
            origin = source;
            destination = dest;
        }


        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);

        }
    }

    public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = new PolylineOptions();
            if (result == null) {
                return;
            }
            if (result.size() < 1) {
                return;
            }

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = result.get(i);
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    if (j == 0) {
                        String line = point.get("distance");
                        if (line != null) {
                            String[] parts = line.split(" ");
                            double distance = Double.parseDouble(parts[0].replace(",", "."));

                            int dis = (int) Math.ceil(distance);

                            if (onDistanceCalculated != null) {
                                onDistanceCalculated.sendDistance(distance);
                            }
                        }
                        continue;

                    } else if (j == 1) {

                        String duration = point.get("duration");
                        if (duration.contains("hours")
                                && (duration.contains("mins") || duration
                                .contains("min"))) {

                            String[] arr = duration.split(" ");
                            int timeDur = 0;
                            for (int k = 0; k < arr.length; k++) {
                                if (k == 0)
                                    timeDur = Integer.parseInt(arr[k]) * 60;
                                if (k == 2)
                                    timeDur = timeDur + Integer.parseInt(arr[k]);

                            }

//                            totalDuration = String.valueOf(timeDur);

                        } else if (duration.contains("mins")
                                || duration.contains("min")) {
                            String[] words = duration.split(" ");
//                            totalDuration = words[0];
                        }
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(5);
                lineOptions.color(Color.BLUE);
            }

            if (routeMap != null && onPolyLineReceived != null) {
                routeMap.addPolyline(lineOptions);
                onPolyLineReceived.onPolyLineReceived(origin, destination, routeMap);
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(origin);
                builder.include(destination);
                LatLngBounds bounds = builder.build();
                int padding = 10;
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,
                        padding);
                // routeMap.moveCamera(cu);
                routeMap.animateCamera(cu);
            }
        }
    }

    public void setPolyLineReceivedListener(OnPolyLineReceived onPolyLineReceived) {
        this.onPolyLineReceived = onPolyLineReceived;
    }

    public interface OnPolyLineReceived {
        void onPolyLineReceived(LatLng origin, LatLng destination, GoogleMap routeMap);
    }
}
