#ifndef FLUTTER_PLUGIN_ISCREENSHOT_PLUGIN_H_
#define FLUTTER_PLUGIN_ISCREENSHOT_PLUGIN_H_

#include <flutter/method_channel.h>
#include <flutter/plugin_registrar_windows.h>

#include <memory>

namespace iscreenshot {

class IscreenshotPlugin : public flutter::Plugin {
 public:
  static void RegisterWithRegistrar(flutter::PluginRegistrarWindows *registrar);

  IscreenshotPlugin();

  virtual ~IscreenshotPlugin();

  // Disallow copy and assign.
  IscreenshotPlugin(const IscreenshotPlugin&) = delete;
  IscreenshotPlugin& operator=(const IscreenshotPlugin&) = delete;

  // Called when a method is called on this plugin's channel from Dart.
  void HandleMethodCall(
      const flutter::MethodCall<flutter::EncodableValue> &method_call,
      std::unique_ptr<flutter::MethodResult<flutter::EncodableValue>> result);
};

}  // namespace iscreenshot

#endif  // FLUTTER_PLUGIN_ISCREENSHOT_PLUGIN_H_
