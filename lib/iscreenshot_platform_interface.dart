import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'iscreenshot_method_channel.dart';

abstract class IscreenshotPlatform extends PlatformInterface {
  /// Constructs a IscreenshotPlatform.
  IscreenshotPlatform() : super(token: _token);

  static final Object _token = Object();

  static IscreenshotPlatform _instance = MethodChannelIscreenshot();

  /// The default instance of [IscreenshotPlatform] to use.
  ///
  /// Defaults to [MethodChannelIscreenshot].
  static IscreenshotPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [IscreenshotPlatform] when
  /// they register themselves.
  static set instance(IscreenshotPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
