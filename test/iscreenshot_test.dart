import 'package:flutter_test/flutter_test.dart';
import 'package:iscreenshot/iscreenshot.dart';
import 'package:iscreenshot/iscreenshot_platform_interface.dart';
import 'package:iscreenshot/iscreenshot_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockIscreenshotPlatform
    with MockPlatformInterfaceMixin
    implements IscreenshotPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final IscreenshotPlatform initialPlatform = IscreenshotPlatform.instance;

  test('$MethodChannelIscreenshot is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelIscreenshot>());
  });

  test('getPlatformVersion', () async {
    Iscreenshot iscreenshotPlugin = Iscreenshot();
    MockIscreenshotPlatform fakePlatform = MockIscreenshotPlatform();
    IscreenshotPlatform.instance = fakePlatform;

    expect(await iscreenshotPlugin.getPlatformVersion(), '42');
  });
}
