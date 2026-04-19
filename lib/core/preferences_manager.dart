import 'package:shared_preferences/shared_preferences.dart';

/// Manages app preferences and settings
class PreferencesManager {
  static const String _keyFavoriteApps = 'favorite_apps';
  static const String _keyRecentApps = 'recent_apps';
  static const int _maxRecentApps = 10;

  late SharedPreferences _prefs;
  bool _initialized = false;

  /// Initialize preferences
  Future<void> init() async {
    if (!_initialized) {
      _prefs = await SharedPreferences.getInstance();
      _initialized = true;
    }
  }

  /// Get favorite app package names
  List<String> getFavoriteApps() {
    _ensureInitialized();
    return _prefs.getStringList(_keyFavoriteApps) ?? [];
  }

  /// Add app to favorites
  Future<bool> addFavorite(String packageName) async {
    _ensureInitialized();
    final favorites = getFavoriteApps();
    if (!favorites.contains(packageName)) {
      favorites.add(packageName);
      return await _prefs.setStringList(_keyFavoriteApps, favorites);
    }
    return true;
  }

  /// Remove app from favorites
  Future<bool> removeFavorite(String packageName) async {
    _ensureInitialized();
    final favorites = getFavoriteApps();
    favorites.remove(packageName);
    return await _prefs.setStringList(_keyFavoriteApps, favorites);
  }

  /// Check if app is favorite
  bool isFavorite(String packageName) {
    return getFavoriteApps().contains(packageName);
  }

  /// Toggle favorite status
  Future<bool> toggleFavorite(String packageName) async {
    if (isFavorite(packageName)) {
      return await removeFavorite(packageName);
    } else {
      return await addFavorite(packageName);
    }
  }

  /// Get recent app package names
  List<String> getRecentApps() {
    _ensureInitialized();
    return _prefs.getStringList(_keyRecentApps) ?? [];
  }

  /// Add app to recent apps
  Future<bool> addRecentApp(String packageName) async {
    _ensureInitialized();
    final recent = getRecentApps();

    // Remove if already exists (to move to front)
    recent.remove(packageName);

    // Add to front
    recent.insert(0, packageName);

    // Keep only max recent apps
    if (recent.length > _maxRecentApps) {
      recent.removeRange(_maxRecentApps, recent.length);
    }

    return await _prefs.setStringList(_keyRecentApps, recent);
  }

  /// Clear recent apps
  Future<bool> clearRecentApps() async {
    _ensureInitialized();
    return await _prefs.remove(_keyRecentApps);
  }

  void _ensureInitialized() {
    if (!_initialized) {
      throw StateError(
        'PreferencesManager not initialized. Call init() first.',
      );
    }
  }
}
