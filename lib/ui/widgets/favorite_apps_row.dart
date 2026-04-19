import 'package:installed_apps/app_info.dart';
import 'package:flutter/material.dart';

/// Horizontal row of favorite apps
class FavoriteAppsRow extends StatelessWidget {
  final List<AppInfo> apps;
  final Function(String) onAppTap;
  final Function(String) onAppLongPress;

  const FavoriteAppsRow({
    super.key,
    required this.apps,
    required this.onAppTap,
    required this.onAppLongPress,
  });

  @override
  Widget build(BuildContext context) {
    if (apps.isEmpty) {
      return Container(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            Icon(
              Icons.star_border,
              size: 48,
              color: Theme.of(
                context,
              ).colorScheme.onSurface.withValues(alpha: 0.3),
            ),
            const SizedBox(height: 8),
            Text(
              'No favorite apps',
              style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                color: Theme.of(
                  context,
                ).colorScheme.onSurface.withValues(alpha: 0.5),
              ),
            ),
            const SizedBox(height: 4),
            Text(
              'Long press any app to add to favorites',
              style: Theme.of(context).textTheme.bodySmall?.copyWith(
                color: Theme.of(
                  context,
                ).colorScheme.onSurface.withValues(alpha: 0.4),
              ),
              textAlign: TextAlign.center,
            ),
          ],
        ),
      );
    }

    return SizedBox(
      height: 100,
      child: ListView.builder(
        scrollDirection: Axis.horizontal,
        padding: const EdgeInsets.symmetric(horizontal: 16),
        itemCount: apps.length,
        itemBuilder: (context, index) {
          final app = apps[index];
          final packageName = app.packageName;
          return Padding(
            padding: const EdgeInsets.only(right: 16),
            child: InkWell(
              onTap: () => onAppTap(packageName),
              onLongPress: () => onAppLongPress(packageName),
              borderRadius: BorderRadius.circular(12),
              child: SizedBox(
                width: 72,
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Container(
                      width: 56,
                      height: 56,
                      decoration: BoxDecoration(
                        borderRadius: BorderRadius.circular(12),
                        boxShadow: [
                          BoxShadow(
                            color: Colors.black.withValues(alpha: 0.2),
                            blurRadius: 4,
                            offset: const Offset(0, 2),
                          ),
                        ],
                      ),
                      child: ClipRRect(
                        borderRadius: BorderRadius.circular(12),
                        child: app.icon != null
                            ? Image.memory(app.icon!, fit: BoxFit.cover)
                            : Container(
                                color: Theme.of(
                                  context,
                                ).colorScheme.primaryContainer,
                                child: Icon(
                                  Icons.android,
                                  color: Theme.of(
                                    context,
                                  ).colorScheme.onPrimaryContainer,
                                ),
                              ),
                      ),
                    ),
                    const SizedBox(height: 8),
                    Text(
                      app.name,
                      maxLines: 1,
                      textAlign: TextAlign.center,
                      overflow: TextOverflow.ellipsis,
                      style: Theme.of(context).textTheme.bodySmall,
                    ),
                  ],
                ),
              ),
            ),
          );
        },
      ),
    );
  }
}
