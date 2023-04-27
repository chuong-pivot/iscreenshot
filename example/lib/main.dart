import 'dart:io';

import 'package:flutter/material.dart';
import 'package:iscreenshot/iscreenshot.dart';

import 'package:path/path.dart';
import 'package:path_provider/path_provider.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String? path;

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: TextButton(
            onPressed: () async {
              final dir = await getApplicationDocumentsDirectory();
              const fileName = 'debug_screenshot.png';
              final savedPath = join(dir.path, fileName);

              await File(savedPath).create(recursive: true);

              final imgpath = await IScreenshot.takeScreenshot(
                saveScreenshotPath: Platform.isIOS ? fileName : savedPath,
              );

              setState(() {
                path = imgpath;
              });
            },
            child: path == null ? Text('hello') : Image.file(File(path!)),
          ),
        ),
      ),
    );
  }
}
