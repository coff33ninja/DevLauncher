import 'package:device_apps/device_apps.dart';
import 'package:flutter/foundation.dart';
import 'preferences_manager.dart';

/// Manages installed applications on the device
class AppManager extends ChangeNotifier {
  final PreferencesManager _prefsManager;

  List<Application> _apps = [];
  List<Application> _filteredApps = [];
  bool _isLoading = false;
  String _searchQuery = '';

  AppManager(this._prefsManager);

  List<Application> get apps => _filteredApps;
  bool get isLoading => _isLoading;
  String get searchQuery => _searchQuery;

  /// Get favorite apps
  List<Application> getFavoriteApps() {
    final favoritePackages = _prefsManager.getFavoriteApps();
    return _apps
        .where((app) => favoritePackages.contains(app.packageName))
        .toList();
  }

  /// Get recent apps
  List<Application> getRecentApps() {
    final recentPackages = _prefsManager.getRecentApps();
    final recentApps = <Application>[];

    // Maintain order from preferences
    for (final packageName in recentPackages) {
      try {
        final app = _apps.firstWhere((app) => app.packageName == packageName);
        recentApps.add(app);
      } catch (e) {
        // App not found, skip
      }
    }

    return recentApps;
  }

  /// Check if app is favorite
  bool isFavorite(String packageName) {
    return _prefsManager.isFavorite(packageName);
  }

  /// Toggle favorite status
  Future<void> toggleFavorite(String packageName) async {
    await _prefsManager.toggleFavorite(packageName);
    notifyListeners();
  }

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
      final success = await DeviceApps.openApp(packageName);
      if (success) {
        // Add to recent apps
        await _prefsManager.addRecentApp(packageName);
      }
      return success;
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
