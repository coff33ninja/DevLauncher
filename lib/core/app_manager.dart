import 'package:device_apps/device_apps.dart';
import 'package:flutter/foundation.dart';

/// Manages installed applications on the device
class AppManager extends ChangeNotifier {
  List<Application> _apps = [];
  List<Application> _filteredApps = [];
  bool _isLoading = false;
  String _searchQuery = '';

  List<Application> get apps => _filteredApps;
  bool get isLoading => _isLoading;
  String get searchQuery => _searchQuery;

  /// Load all installed applications
  Future<void> loadApps() async {
    _isLoading = true;
    notifyListeners();

    try {
      // Get all apps with launch intent (launchable apps)
      _apps = await DeviceApps.getInstalledApplications(
        includeAppIcons: true,
        includeSystemApps: true,
        onlyAppsWithLaunchIntent: true,
      );

      // Sort alphabetically
      _apps.sort(
        (a, b) => a.appName.toLowerCase().compareTo(b.appName.toLowerCase()),
      );

      _filteredApps = List.from(_apps);
    } catch (e) {
      debugPrint('Error loading apps: $e');
      _apps = [];
      _filteredApps = [];
    }

    _isLoading = false;
    notifyListeners();
  }

  /// Search/filter apps by query
  void searchApps(String query) {
    _searchQuery = query.toLowerCase();

    if (_searchQuery.isEmpty) {
      _filteredApps = List.from(_apps);
    } else {
      _filteredApps = _apps.where((app) {
        return app.appName.toLowerCase().contains(_searchQuery) ||
            app.packageName.toLowerCase().contains(_searchQuery);
      }).toList();
    }

    notifyListeners();
  }

  /// Launch an application
  Future<bool> launchApp(String packageName) async {
    try {
      return await DeviceApps.openApp(packageName);
    } catch (e) {
      debugPrint('Error launching app: $e');
      return false;
    }
  }

  /// Open app settings
  Future<bool> openAppSettings(String packageName) async {
    try {
      return await DeviceApps.openAppSettings(packageName);
    } catch (e) {
      debugPrint('Error opening app settings: $e');
      return false;
    }
  }

  /// Get app by package name
  Application? getApp(String packageName) {
    try {
      return _apps.firstWhere((app) => app.packageName == packageName);
    } catch (e) {
      return null;
    }
  }

  /// Refresh app list
  Future<void> refresh() async {
    await loadApps();
  }
}
