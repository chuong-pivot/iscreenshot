#include "include/iscreenshot/iscreenshot_plugin_c_api.h"

#include <flutter/plugin_registrar_windows.h>

#include "iscreenshot_plugin.h"

void IscreenshotPluginCApiRegisterWithRegistrar(
    FlutterDesktopPluginRegistrarRef registrar) {
  iscreenshot::IscreenshotPlugin::RegisterWithRegistrar(
      flutter::PluginRegistrarManager::GetInstance()
          ->GetRegistrar<flutter::PluginRegistrarWindows>(registrar));
}
