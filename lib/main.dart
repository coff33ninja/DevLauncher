import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:provider/provider.dart';
import 'core/app_manager.dart';
import 'core/preferences_manager.dart';
import 'ui/screens/app_drawer_screen.dart';
import 'ui/widgets/app_list_item.dart';
import 'ui/widgets/favorite_apps_row.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  // Initialize preferences
  final prefsManager = PreferencesManager();
  await prefsManager.init();

  // Make the app fullscreen and hide system UI
  SystemChrome.setEnabledSystemUIMode(SystemUiMode.immersiveSticky);

  runApp(
    MultiProvider(
      providers: [
        Provider<PreferencesManager>.value(value: prefsManager),
        ChangeNotifierProvider(create: (context) => AppManager(prefsManager)),
      ],
      child: const DevLauncherApp(),
    ),
  );
}

class DevLauncherApp extends StatelessWidget {
  const DevLauncherApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Dev Launcher',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(
          seedColor: Colors.deepPurple,
          brightness: Brightness.dark,
        ),
        useMaterial3: true,
      ),
      home: const LauncherHome(),
    );
  }
}

class LauncherHome extends StatefulWidget {
  const LauncherHome({super.key});

  @override
  State<LauncherHome> createState() => _LauncherHomeState();
}

class _LauncherHomeState extends State<LauncherHome> {
  final TextEditingController _searchController = TextEditingController();
  bool _showSearch = false;

  @override
  void initState() {
    super.initState();
    // Load apps on startup
    WidgetsBinding.instance.addPostFrameCallback((_) {
      Provider.of<AppManager>(context, listen: false).loadApps();
    });
  }

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  void _toggleSearch() {
    setState(() {
      _showSearch = !_showSearch;
      if (!_showSearch) {
        _searchController.clear();
        Provider.of<AppManager>(context, listen: false).searchApps('');
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: GestureDetector(
        onVerticalDragUpdate: (details) {
          // Swipe up to show search
          if (details.delta.dy < -10 && !_showSearch) {
            _toggleSearch();
          }
        },
        child: Stack(
          children: [
            // Main launcher content
            Center(
              child: SingleChildScrollView(
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Icon(
                      Icons.rocket_launch,
                      size: 80,
                      color: Theme.of(context).colorScheme.primary,
                    ),
                    const SizedBox(height: 24),
                    Text(
                      'Dev Launcher',
                      style: Theme.of(context).textTheme.headlineLarge
                          ?.copyWith(fontWeight: FontWeight.bold),
                    ),
                    const SizedBox(height: 8),
                    Text(
                      'Swipe up to search',
                      style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                        color: Theme.of(
                          context,
                        ).colorScheme.onSurface.withValues(alpha: 0.6),
                      ),
                    ),
                    const SizedBox(height: 48),

                    // Favorite apps section
                    Consumer<AppManager>(
                      builder: (context, appManager, child) {
                        final favoriteApps = appManager.getFavoriteApps();
                        return Column(
                          children: [
                            if (favoriteApps.isNotEmpty)
                              Padding(
                                padding: const EdgeInsets.only(
                                  left: 16,
                                  bottom: 8,
                                ),
                                child: Align(
                                  alignment: Alignment.centerLeft,
                                  child: Text(
                                    'Favorites',
                                    style: Theme.of(context)
                                        .textTheme
                                        .titleMedium
                                        ?.copyWith(fontWeight: FontWeight.bold),
                                  ),
                                ),
                              ),
                            FavoriteAppsRow(
                              apps: favoriteApps,
                              onAppTap: (packageName) async {
                                await appManager.launchApp(packageName);
                              },
                              onAppLongPress: (packageName) {
                                _showAppOptions(context, packageName);
                              },
                            ),
                          ],
                        );
                      },
                    ),

                    const SizedBox(height: 24),
                    _buildQuickActions(context),
                  ],
                ),
              ),
            ),

            // Search overlay
            if (_showSearch) _buildSearchOverlay(context),
          ],
        ),
      ),
    );
  }

  Widget _buildQuickActions(BuildContext context) {
    return Wrap(
      spacing: 16,
      runSpacing: 16,
      children: [
        _buildActionChip(
          context,
          icon: Icons.apps,
          label: 'Apps',
          onTap: () {
            Navigator.push(
              context,
              MaterialPageRoute(builder: (context) => const AppDrawerScreen()),
            );
          },
        ),
        _buildActionChip(
          context,
          icon: Icons.settings,
          label: 'Settings',
          onTap: () {
            ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(content: Text('Settings coming soon')),
            );
          },
        ),
        _buildActionChip(
          context,
          icon: Icons.extension,
          label: 'Plugins',
          onTap: () {
            ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(content: Text('Plugin system coming soon')),
            );
          },
        ),
      ],
    );
  }

  Widget _buildActionChip(
    BuildContext context, {
    required IconData icon,
    required String label,
    required VoidCallback onTap,
  }) {
    return ActionChip(
      avatar: Icon(icon, size: 20),
      label: Text(label),
      onPressed: onTap,
      backgroundColor: Theme.of(context).colorScheme.surfaceContainerHighest,
    );
  }

  Widget _buildSearchOverlay(BuildContext context) {
    return Container(
      color: Theme.of(context).colorScheme.surface.withValues(alpha: 0.95),
      child: SafeArea(
        child: Column(
          children: [
            Padding(
              padding: const EdgeInsets.all(16.0),
              child: Row(
                children: [
                  Expanded(
                    child: TextField(
                      controller: _searchController,
                      autofocus: true,
                      decoration: InputDecoration(
                        hintText: 'Search apps, commands...',
                        prefixIcon: const Icon(Icons.search),
                        border: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(12),
                        ),
                        filled: true,
                      ),
                      onChanged: (query) {
                        Provider.of<AppManager>(
                          context,
                          listen: false,
                        ).searchApps(query);
                      },
                      onSubmitted: (value) {
                        // Launch first result if available
                        final appManager = Provider.of<AppManager>(
                          context,
                          listen: false,
                        );
                        if (appManager.apps.isNotEmpty) {
                          appManager.launchApp(
                            appManager.apps.first.packageName,
                          );
                          _toggleSearch();
                        }
                      },
                    ),
                  ),
                  const SizedBox(width: 8),
                  IconButton(
                    icon: const Icon(Icons.close),
                    onPressed: _toggleSearch,
                  ),
                ],
              ),
            ),
            Expanded(
              child: Consumer<AppManager>(
                builder: (context, appManager, child) {
                  if (appManager.isLoading) {
                    return const Center(child: CircularProgressIndicator());
                  }

                  if (appManager.apps.isEmpty) {
                    return Center(
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Icon(
                            Icons.search_off,
                            size: 64,
                            color: Theme.of(
                              context,
                            ).colorScheme.onSurface.withValues(alpha: 0.3),
                          ),
                          const SizedBox(height: 16),
                          Text(
                            appManager.searchQuery.isEmpty
                                ? 'No apps found'
                                : 'No results for "${appManager.searchQuery}"',
                            style: Theme.of(context).textTheme.bodyLarge
                                ?.copyWith(
                                  color: Theme.of(context).colorScheme.onSurface
                                      .withValues(alpha: 0.5),
                                ),
                          ),
                        ],
                      ),
                    );
                  }

                  return ListView.builder(
                    itemCount: appManager.apps.length,
                    itemBuilder: (context, index) {
                      final app = appManager.apps[index];
                      return AppListItem(
                        app: app,
                        onTap: () async {
                          final launched = await appManager.launchApp(
                            app.packageName,
                          );
                          if (launched == true) {
                            _toggleSearch();
                          } else if (context.mounted) {
                            ScaffoldMessenger.of(context).showSnackBar(
                              SnackBar(
                                content: Text('Failed to launch ${app.name}'),
                              ),
                            );
                          }
                        },
                        onLongPress: () {
                          _showAppOptions(context, app.packageName);
                        },
                      );
                    },
                  );
                },
              ),
            ),
          ],
        ),
      ),
    );
  }

  void _showAppOptions(BuildContext context, String packageName) {
    final appManager = Provider.of<AppManager>(context, listen: false);
    final app = appManager.getApp(packageName);
    final isFavorite = appManager.isFavorite(packageName);

    showModalBottomSheet(
      context: context,
      builder: (context) => SafeArea(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            if (app != null)
              ListTile(
                title: Text(
                  app.name,
                  style: Theme.of(context).textTheme.titleMedium,
                ),
                subtitle: Text(
                  packageName,
                  style: Theme.of(context).textTheme.bodySmall?.copyWith(
                    color: Theme.of(
                      context,
                    ).colorScheme.onSurface.withValues(alpha: 0.6),
                  ),
                ),
              ),
            const Divider(),
            ListTile(
              leading: Icon(isFavorite ? Icons.star : Icons.star_border),
              title: Text(
                isFavorite ? 'Remove from favorites' : 'Add to favorites',
              ),
              onTap: () async {
                await appManager.toggleFavorite(packageName);
                if (context.mounted) {
                  Navigator.pop(context);
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(
                      content: Text(
                        isFavorite
                            ? 'Removed from favorites'
                            : 'Added to favorites',
                      ),
                    ),
                  );
                }
              },
            ),
            ListTile(
              leading: const Icon(Icons.info_outline),
              title: const Text('App info'),
              onTap: () async {
                Navigator.pop(context);
                await appManager.openAppSettings(packageName);
              },
            ),
          ],
        ),
      ),
    );
  }
}
