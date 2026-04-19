import 'package:installed_apps/app_info.dart';
import 'package:installed_apps/installed_apps.dart';
import 'package:flutter/foundation.dart';
import 'preferences_manager.dart';

/// Manages installed applications on the device
class AppManager extends ChangeNotifier {
  final PreferencesManager _prefsManager;

  List<AppInfo> _apps = [];
  List<AppInfo> _filteredApps = [];
  bool _isLoading = false;
  String _searchQuery = '';

  AppManager(this._prefsManager);

  List<AppInfo> get apps => _filteredApps;
  bool get isLoading => _isLoading;
  String get searchQuery => _searchQuery;

  /// Get favorite apps
  List<AppInfo> getFavoriteApps() {
    final favoritePackages = _prefsManager.getFavoriteApps();
    return _apps
        .where((app) => favoritePackages.contains(app.packageName))
        .toList();
  }

  /// Get recent apps
  List<AppInfo> getRecentApps() {
    final recentPackages = _prefsManager.getRecentApps();
    final recentApps = <AppInfo>[];

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
      // Get all apps
      _apps = await InstalledApps.getInstalledApps();

      // Sort alphabetically
      _apps.sort(
        (a, b) => a.name.toLowerCase().compareTo(b.name.toLowerCase()),
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
        return app.name.toLowerCase().contains(_searchQuery) ||
            app.packageName.toLowerCase().contains(_searchQuery);
      }).toList();
    }

    notifyListeners();
  }

  /// Launch an application
  Future<bool?> launchApp(String packageName) async {
    try {
      final success = await InstalledApps.startApp(packageName);
      if (success == true) {
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
  Future<void> openAppSettings(String packageName) async {
    try {
      InstalledApps.openSettings(packageName);
    } catch (e) {
      debugPrint('Error opening app settings: $e');
    }
  }

  /// Get app by package name
  AppInfo? getApp(String packageName) {
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
