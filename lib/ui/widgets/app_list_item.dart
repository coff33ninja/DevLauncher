import 'package:device_apps/device_apps.dart';
import 'package:flutter/material.dart';

/// List item widget for displaying apps in search results
class AppListItem extends StatelessWidget {
  final Application app;
  final VoidCallback onTap;
  final VoidCallback? onLongPress;

  const AppListItem({
    super.key,
    required this.app,
    required this.onTap,
    this.onLongPress,
  });

  @override
  Widget build(BuildContext context) {
    return ListTile(
      leading: Container(
        width: 48,
        height: 48,
        decoration: BoxDecoration(borderRadius: BorderRadius.circular(8)),
        child: ClipRRect(
          borderRadius: BorderRadius.circular(8),
          child: app is ApplicationWithIcon
              ? Image.memory(
                  (app as ApplicationWithIcon).icon,
                  fit: BoxFit.cover,
                )
              : Container(
                  color: Theme.of(context).colorScheme.primaryContainer,
                  child: Icon(
                    Icons.android,
                    color: Theme.of(context).colorScheme.onPrimaryContainer,
                  ),
                ),
        ),
      ),
      title: Text(app.appName, maxLines: 1, overflow: TextOverflow.ellipsis),
      subtitle: Text(
        app.packageName,
        maxLines: 1,
        overflow: TextOverflow.ellipsis,
        style: Theme.of(context).textTheme.bodySmall?.copyWith(
          color: Theme.of(context).colorScheme.onSurface.withValues(alpha: 0.6),
        ),
      ),
      onTap: onTap,
      onLongPress: onLongPress,
    );
  }
}
