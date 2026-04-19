// This is a basic Flutter widget test.
//
// To perform an interaction with a widget in your test, use the WidgetTester
// utility in the flutter_test package. For example, you can send tap and scroll
// gestures. You can also use WidgetTester to find child widgets in the widget
// tree, read text, and verify that the values of widget properties are correct.

import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:provider/provider.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'package:dev_launcher/main.dart';
import 'package:dev_launcher/core/app_manager.dart';
import 'package:dev_launcher/core/preferences_manager.dart';

void main() {
  testWidgets('Launcher smoke test', (WidgetTester tester) async {
    // Initialize mock preferences
    SharedPreferences.setMockInitialValues({});
    final prefsManager = PreferencesManager();
    await prefsManager.init();

    // Build our app and trigger a frame.
    await tester.pumpWidget(
      MultiProvider(
        providers: [
          Provider<PreferencesManager>.value(value: prefsManager),
          ChangeNotifierProvider(create: (context) => AppManager(prefsManager)),
        ],
        child: const DevLauncherApp(),
      ),
    );

    // Verify that our launcher starts with the correct title.
    expect(find.text('Dev Launcher'), findsAtLeastNWidgets(1));
    expect(find.text('Swipe up to search'), findsOneWidget);

    // Verify that we have some quick action chips
    expect(find.text('Apps'), findsOneWidget);
    expect(find.text('Settings'), findsOneWidget);
  });
}
