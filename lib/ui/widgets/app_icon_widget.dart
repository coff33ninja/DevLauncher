import 'package:installed_apps/app_info.dart';
import 'package:flutter/material.dart';

/// Widget to display an app icon with name
class AppIconWidget extends StatelessWidget {
  final AppInfo app;
  final VoidCallback onTap;
  final VoidCallback? onLongPress;

  const AppIconWidget({
    super.key,
    required this.app,
    required this.onTap,
    this.onLongPress,
  });

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: onTap,
      onLongPress: onLongPress,
      borderRadius: BorderRadius.circular(12),
      child: Padding(
        padding: const EdgeInsets.all(8.0),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            // App icon
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
                        color: Theme.of(context).colorScheme.primaryContainer,
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
            // App name
            Text(
              app.name,
              maxLines: 2,
              textAlign: TextAlign.center,
              overflow: TextOverflow.ellipsis,
              style: Theme.of(context).textTheme.bodySmall,
            ),
          ],
        ),
      ),
    );
  }
}
