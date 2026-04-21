package com.smartbus.app.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.smartbus.app.R;
import com.smartbus.app.activities.MainActivity;
import com.smartbus.app.database.DBHelper;
import com.smartbus.app.models.BusPass;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class TrackFragment extends Fragment {

    private MapView mapView;
    private IMapController mapController;
    private FusedLocationProviderClient fusedLocationClient;
    private Handler simulationHandler;
    private final Random random = new Random();

    private Marker userMarker;
    private Marker busMarker;
    private Polyline routePolyline;
    private GeoPoint userGeoPoint;
    private GeoPoint busGeoPoint;

    private TextView tvDistance, tvSpeed, tvEta, tvUpcoming, tvBusName, tvBusRoute;
    private final List<GeoPoint> currentRoutePath = new ArrayList<>();
    private int currentPointIndex = 0;
    private final List<Marker> stopMarkers = new ArrayList<>();

    private String currentSearchedRoute = "Wani ↔ Yavatmal";

    // Real approximate coordinates for known city stops
    private static final Map<String, GeoPoint> CITY_COORDS = new HashMap<>();
    static {
        CITY_COORDS.put("Wani", new GeoPoint(20.0565, 78.9499));
        CITY_COORDS.put("Yavatmal", new GeoPoint(20.3888, 78.1204));
        CITY_COORDS.put("Chandrapur", new GeoPoint(19.9615, 79.2961));
        CITY_COORDS.put("Nagpur", new GeoPoint(21.1458, 79.0882));
        CITY_COORDS.put("Amravati", new GeoPoint(20.9320, 77.7523));
        CITY_COORDS.put("Warora", new GeoPoint(20.2291, 79.0044));
        CITY_COORDS.put("Pandharkawada", new GeoPoint(20.0278, 78.5165));
        CITY_COORDS.put("Zari", new GeoPoint(20.0811, 78.6534));
        CITY_COORDS.put("Maregaon", new GeoPoint(20.3167, 78.6833));
    }

    private static final String[] LOCATIONS = {
            "Wani", "Yavatmal", "Chandrapur", "Nagpur", "Amravati",
            "Warora", "Pandharkawada", "Zari", "Maregaon"
    };

    private final Runnable busMoveRunnable = new Runnable() {
        @Override
        public void run() {
            advanceBusOnPath();
            if (simulationHandler != null) {
                simulationHandler.postDelayed(this, 2500);
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Context ctx = requireContext();
            // Fix: pass real SharedPreferences so OSMDroid can persist tile cache config
            Configuration.getInstance().load(ctx,
                    ctx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE));
            Configuration.getInstance().setUserAgentValue(ctx.getPackageName());
        } catch (Exception e) {
            /* fail silently */ }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_track, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
            simulationHandler = new Handler(Looper.getMainLooper());

            // Bottom sheet UI references
            tvDistance = view.findViewById(R.id.tv_dist_val_track);
            tvSpeed = view.findViewById(R.id.tv_speed_val_track);
            tvEta = view.findViewById(R.id.tv_eta_val_track);
            tvUpcoming = view.findViewById(R.id.tv_upcoming_stops_track);
            tvBusName = view.findViewById(R.id.tv_bus_name_track);
            tvBusRoute = view.findViewById(R.id.tv_bus_route_track);

            // Initialise BottomSheetBehavior so the sheet is draggable
            View bottomSheet = view.findViewById(R.id.bottom_sheet_track);
            if (bottomSheet != null) {
                BottomSheetBehavior<View> bsb = BottomSheetBehavior.from(bottomSheet);
                bsb.setPeekHeight(300);
                bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }

            setupMapView(view);
            setupSearchBar(view);

            // View Full Route button
            View btnViewRoute = view.findViewById(R.id.btn_view_full_route);
            if (btnViewRoute != null) {
                btnViewRoute.setOnClickListener(v -> {
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).loadSubFragment(
                                RouteDetailsFragment.newInstance(currentSearchedRoute));
                    }
                });
            }
            // Map layer toggle
            if (view.findViewById(R.id.btn_layers_track) != null) {
                view.findViewById(R.id.btn_layers_track).setOnClickListener(v -> cycleMapProviders());
            }
            // Re-center on user location
            if (view.findViewById(R.id.btn_my_location_track) != null) {
                view.findViewById(R.id.btn_my_location_track).setOnClickListener(v -> fetchLocation());
            }

            // Load user's active pass route from DB, then fetch location
            loadActivePassRoute();
            fetchLocation();

        } catch (Exception e) {
            /* fail silently */ }
    }

    // ── Map setup ────────────────────────────────────────────────────────────
    private void setupMapView(View view) {
        mapView = view.findViewById(R.id.map_view_track);
        if (mapView == null)
            return;
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapController = mapView.getController();
        mapController.setZoom(14.0);
        // Default centre: Wani
        mapController.setCenter(CITY_COORDS.get("Wani"));
    }

    // ── Search bar ───────────────────────────────────────────────────────────
    private void setupSearchBar(View view) {
        AutoCompleteTextView etSearch = view.findViewById(R.id.et_track_search);
        if (etSearch == null)
            return;

        etSearch.setTextColor(getResources().getColor(R.color.text_primary, null));
        etSearch.setHintTextColor(getResources().getColor(R.color.text_secondary, null));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), R.layout.item_dropdown_black, LOCATIONS);
        etSearch.setAdapter(adapter);

        etSearch.setOnItemClickListener((parent, v, position, id) -> {
            String loc = (String) parent.getItemAtPosition(position);
            handleQuickSearch(loc, view);
        });
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            String loc = etSearch.getText().toString().trim();
            if (!TextUtils.isEmpty(loc))
                handleQuickSearch(loc, view);
            return true;
        });
    }

    // ── Load user's current pass from DB ─────────────────────────────────────
    private void loadActivePassRoute() {
        if (!isAdded())
            return;
        try {
            DBHelper db = new DBHelper(requireContext());
            BusPass pass = db.getLatestPass();
            if (pass != null) {
                currentSearchedRoute = pass.getRoute();
                // Parse "Source - Destination" or "Source ↔ Destination"
                String[] parts = pass.getRoute().split("[↔\\-–]");
                String src = parts.length > 0 ? parts[0].trim() : "Wani";
                String dest = parts.length > 1 ? parts[1].trim() : "Yavatmal";
                String routeLabel = src + " ↔ " + dest;

                if (tvBusRoute != null)
                    tvBusRoute.setText(routeLabel);

                GeoPoint startPt = getCoords(src);
                GeoPoint endPt = getCoords(dest);
                initRoute("MSRTC Express: " + routeLabel, startPt, endPt);
            }
        } catch (Exception e) {
            // No pass yet — use default route
            initRoute("MSRTC Super-Fast Express",
                    getCoords("Wani"), getCoords("Yavatmal"));
        }
    }

    // ── Quick search handler ─────────────────────────────────────────────────
    private void handleQuickSearch(String loc, View view) {
        if (!isAdded() || mapController == null)
            return;

        String srcCity = "Wani";
        currentSearchedRoute = srcCity + " ↔ " + loc;

        GeoPoint target = getCoords(loc);
        GeoPoint src = getCoords(srcCity);

        mapController.animateTo(target);
        mapController.setZoom(13.0);

        String routeLabel = srcCity + " ↔ " + loc;
        if (tvBusRoute != null)
            tvBusRoute.setText(routeLabel);

        initRoute("MSRTC Express: " + routeLabel, src, target);
        updateBusStack(view, loc);

        int buses = 3 + random.nextInt(5);
        Toast.makeText(getContext(),
                buses + " buses found on " + loc + " route. Tracking nearest.",
                Toast.LENGTH_LONG).show();
    }

    // ── Bus stack chips (dynamic) ─────────────────────────────────────────────
    private void updateBusStack(View view, String loc) {
        if (view == null)
            return;
        LinearLayout stackContainer = view.findViewById(R.id.layout_bus_stack);
        if (stackContainer == null)
            return;

        stackContainer.removeAllViews();
        String[] types = { "Shivshahi", "Express", "Asiad", "Parivartan" };

        for (String type : types) {
            com.google.android.material.chip.Chip chip = new com.google.android.material.chip.Chip(getContext());
            chip.setText(type + " · Ready");
            chip.setChipIcon(ContextCompat.getDrawable(getContext(),
                    android.R.drawable.ic_menu_directions));
            chip.setCheckable(false);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 16, 0);
            chip.setLayoutParams(params);

            chip.setOnClickListener(v -> {
                Toast.makeText(getContext(),
                        "Switching to " + type + " tracking…", Toast.LENGTH_SHORT).show();
                GeoPoint src = getCoords("Wani");
                GeoPoint dest = getCoords(loc);
                initRoute("MSRTC " + type + ": Wani ↔ " + loc, src, dest);
            });
            stackContainer.addView(chip);
        }
    }

    // ── Location fetch ───────────────────────────────────────────────────────
    private void fetchLocation() {
        if (!isAdded())
            return;
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, 101);
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (!isAdded())
                return;
            userGeoPoint = (location != null)
                    ? new GeoPoint(location.getLatitude(), location.getLongitude())
                    : getCoords("Wani"); // sensible default
            updateUserMarker();
            mapController.animateTo(userGeoPoint);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchLocation();
        }
    }

    private void updateUserMarker() {
        if (mapView == null || !isAdded() || userGeoPoint == null)
            return;
        if (userMarker == null) {
            userMarker = new Marker(mapView);
            userMarker.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_locate));
            userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
            userMarker.setTitle("You are here");
            mapView.getOverlays().add(userMarker);
        }
        userMarker.setPosition(userGeoPoint);
        mapView.invalidate();
    }

    // ── Route initialisation and simulation ──────────────────────────────────
    private void initRoute(String busLabel, GeoPoint start, GeoPoint end) {
        if (!isAdded() || start == null || end == null)
            return;
        if (simulationHandler != null)
            simulationHandler.removeCallbacks(busMoveRunnable);

        currentRoutePath.clear();
        int steps = 40;
        double latDiff = (end.getLatitude() - start.getLatitude()) / steps;
        double lngDiff = (end.getLongitude() - start.getLongitude()) / steps;

        for (int i = 0; i <= steps; i++) {
            // Small random wobble makes the path look like a real road
            double wobbleLat = (i > 0 && i < steps) ? (random.nextDouble() - 0.5) * 0.002 : 0;
            double wobbleLng = (i > 0 && i < steps) ? (random.nextDouble() - 0.5) * 0.002 : 0;
            currentRoutePath.add(new GeoPoint(
                    start.getLatitude() + latDiff * i + wobbleLat,
                    start.getLongitude() + lngDiff * i + wobbleLng));
        }

        currentPointIndex = 0;
        busGeoPoint = currentRoutePath.get(0);

        if (mapView != null) {
            // Remove old route overlay
            if (routePolyline != null)
                mapView.getOverlays().remove(routePolyline);
            routePolyline = new Polyline();
            routePolyline.setPoints(currentRoutePath);
            routePolyline.getOutlinePaint().setColor(0xFFFF5252);
            routePolyline.getOutlinePaint().setStrokeWidth(12f);
            mapView.getOverlays().add(routePolyline);

            // Stop markers at Start, Mid, End
            clearStopMarkers();
            addStop(currentRoutePath.get(0), "Departure Stop");
            addStop(currentRoutePath.get(steps / 2), "Mid Stop");
            addStop(currentRoutePath.get(steps), "Arrival Stop");

            updateBusUI(busLabel);
            startSimulation();
            mapView.invalidate();
        }
    }

    private void addStop(GeoPoint p, String name) {
        if (mapView == null || !isAdded())
            return;
        Marker m = new Marker(mapView);
        m.setPosition(p);
        m.setTitle(name);
        m.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.bg_notif_dot));
        m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        mapView.getOverlays().add(m);
        stopMarkers.add(m);
    }

    private void clearStopMarkers() {
        if (mapView == null)
            return;
        for (Marker m : stopMarkers)
            mapView.getOverlays().remove(m);
        stopMarkers.clear();
    }

    private void updateBusUI(String label) {
        if (mapView == null || !isAdded())
            return;
        if (busMarker == null) {
            busMarker = new Marker(mapView);
            busMarker.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_bus_front));
            busMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            mapView.getOverlays().add(busMarker);
        }
        busMarker.setTitle(label);
        busMarker.setPosition(busGeoPoint);
        if (tvBusName != null)
            tvBusName.setText(label);
    }

    private void advanceBusOnPath() {
        if (currentRoutePath.isEmpty() || !isAdded() || busMarker == null)
            return;

        currentPointIndex = (currentPointIndex + 1) % currentRoutePath.size();
        busGeoPoint = currentRoutePath.get(currentPointIndex);
        busMarker.setPosition(busGeoPoint);

        // Distance from bus to user
        double dist = (userGeoPoint != null)
                ? busGeoPoint.distanceToAsDouble(userGeoPoint) / 1000.0
                : 0.0;

        if (tvDistance != null)
            tvDistance.setText(String.format(Locale.getDefault(), "%.1f km", dist));

        // ETA: assume 30 km/h average
        int etaMin = (int) Math.ceil(dist / 30.0 * 60);
        if (tvEta != null)
            tvEta.setText(etaMin > 0 ? etaMin + " min" : "Arriving");

        // Speed with punctuality status
        if (tvSpeed != null) {
            int speed = 32 + random.nextInt(18);
            String status = (random.nextInt(4) == 0) ? " · DELAY" : " · ON TIME";
            tvSpeed.setText(speed + " km/h" + status);
        }

        // Upcoming stops text
        if (tvUpcoming != null) {
            int total = currentRoutePath.size();
            String crowdTag = (random.nextInt(3) == 0) ? " [Crowded]" : " [Seats Free]";
            if (currentPointIndex < total / 2) {
                tvUpcoming.setText("• Mid Stop (Next)" + crowdTag + "\n• Arrival Stop");
            } else {
                tvUpcoming.setText("• Arrival Stop (Next) [Last]" + crowdTag);
            }
        }

        if (mapView != null)
            mapView.invalidate();
    }

    private void startSimulation() {
        if (simulationHandler != null)
            simulationHandler.post(busMoveRunnable);
    }

    // ── Map layer toggle ─────────────────────────────────────────────────────
    private void cycleMapProviders() {
        if (mapView == null)
            return;
        if (mapView.getTileProvider().getTileSource() == TileSourceFactory.MAPNIK) {
            mapView.setTileSource(TileSourceFactory.USGS_SAT);
            showToast("Satellite View");
        } else {
            mapView.setTileSource(TileSourceFactory.MAPNIK);
            showToast("Standard Map View");
        }
        mapView.invalidate();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────
    /** Returns real GeoPoint for a city name, or a default near Wani if unknown. */
    private GeoPoint getCoords(String city) {
        GeoPoint pt = CITY_COORDS.get(city);
        return (pt != null) ? pt : new GeoPoint(20.0565, 78.9499);
    }

    private void showToast(String msg) {
        if (getContext() != null)
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    // ── Lifecycle ────────────────────────────────────────────────────────────
    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null)
            mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null)
            mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (simulationHandler != null)
            simulationHandler.removeCallbacksAndMessages(null);
        if (mapView != null)
            mapView.onDetach();
    }
}
