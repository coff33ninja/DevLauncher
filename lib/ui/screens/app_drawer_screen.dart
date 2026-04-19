import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../core/app_manager.dart';
import '../widgets/app_icon_widget.dart';

/// Full screen app drawer showing all installed apps
class AppDrawerScreen extends StatefulWidget {
  const AppDrawerScreen({super.key});

  @override
  State<AppDrawerScreen> createState() => _AppDrawerScreenState();
}

class _AppDrawerScreenState extends State<AppDrawerScreen> {
  final TextEditingController _searchController = TextEditingController();

  @override
  void initState() {
    super.initState();
    // Load apps when screen opens
    WidgetsBinding.instance.addPostFrameCallback((_) {
      final appManager = context.read<AppManager>();
      if (appManager.apps.isEmpty) {
        appManager.loadApps();
      }
    });
  }

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('All Apps'),
        bottom: PreferredSize(
          preferredSize: const Size.fromHeight(64),
          child: Padding(
            padding: const EdgeInsets.all(8.0),
            child: TextField(
              controller: _searchController,
              decoration: InputDecoration(
                hintText: 'Search apps...',
                prefixIcon: const Icon(Icons.search),
                suffixIcon: _searchController.text.isNotEmpty
                    ? IconButton(
                        icon: const Icon(Icons.clear),
                        onPressed: () {
                          _searchController.clear();
                          Provider.of<AppManager>(
                            context,
                            listen: false,
                          ).searchApps('');
                        },
                      )
                    : null,
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
                setState(() {});
              },
            ),
          ),
        ),
      ),
      body: Consumer<AppManager>(
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
                    Icons.apps,
                    size: 64,
                    color: Theme.of(
                      context,
                    ).colorScheme.onSurface.withValues(alpha: 0.3),
                  ),
                  const SizedBox(height: 16),
                  Text(
                    'No apps found',
                    style: Theme.of(context).textTheme.titleLarge,
                  ),
                  const SizedBox(height: 8),
                  TextButton.icon(
                    onPressed: () => appManager.refresh(),
                    icon: const Icon(Icons.refresh),
                    label: const Text('Refresh'),
                  ),
                ],
              ),
            );
          }

          return GridView.builder(
            padding: const EdgeInsets.all(16),
            gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
              crossAxisCount: 4,
              childAspectRatio: 0.85,
              crossAxisSpacing: 8,
              mainAxisSpacing: 8,
            ),
            itemCount: appManager.apps.length,
            itemBuilder: (context, index) {
              final app = appManager.apps[index];
              return AppIconWidget(
                app: app,
                onTap: () async {
                  final launched = await appManager.launchApp(app.packageName);
                  if (launched != true && context.mounted) {
                    ScaffoldMessenger.of(context).showSnackBar(
                      SnackBar(content: Text('Failed to launch ${app.name}')),
                    );
                  }
                },
                onLongPress: () {
                  _showAppOptions(context, app.packageName, app.name);
                },
              );
            },
          );
        },
      ),
    );
  }

  void _showAppOptions(
    BuildContext context,
    String packageName,
    String appName,
  ) {
    showModalBottomSheet(
      context: context,
      builder: (context) => SafeArea(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            ListTile(
              leading: const Icon(Icons.info_outline),
              title: const Text('App Info'),
              onTap: () async {
                Navigator.pop(context);
                await Provider.of<AppManager>(
                  context,
                  listen: false,
                ).openAppSettings(packageName);
              },
            ),
            ListTile(
              title: Text(
                appName,
                style: Theme.of(context).textTheme.bodySmall?.copyWith(
                  color: Theme.of(
                    context,
                  ).colorScheme.onSurface.withValues(alpha: 0.6),
                ),
              ),
              subtitle: Text(
                packageName,
                style: Theme.of(context).textTheme.bodySmall?.copyWith(
                  color: Theme.of(
                    context,
                  ).colorScheme.onSurface.withValues(alpha: 0.4),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
