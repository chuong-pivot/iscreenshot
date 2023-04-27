import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'iscreenshot_platform_interface.dart';

/// An implementation of [IscreenshotPlatform] that uses method channels.
class MethodChannelIscreenshot extends IscreenshotPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('iscreenshot');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
