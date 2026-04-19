import 'package:installed_apps/app_info.dart';
import 'package:flutter/material.dart';

/// List item widget for displaying apps in search results
class AppListItem extends StatelessWidget {
  final AppInfo app;
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
          child: app.icon != null
              ? Image.memory(app.icon!, fit: BoxFit.cover)
              : Container(
                  color: Theme.of(context).colorScheme.primaryContainer,
                  child: Icon(
                    Icons.android,
                    color: Theme.of(context).colorScheme.onPrimaryContainer,
                  ),
                ),
        ),
      ),
      title: Text(app.name, maxLines: 1, overflow: TextOverflow.ellipsis),
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
