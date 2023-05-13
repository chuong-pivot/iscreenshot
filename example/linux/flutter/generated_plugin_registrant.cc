//
//  Generated file. Do not edit.
//

// clang-format off

#include "generated_plugin_registrant.h"

#include <iscreenshot/iscreenshot_plugin.h>

void fl_register_plugins(FlPluginRegistry* registry) {
  g_autoptr(FlPluginRegistrar) iscreenshot_registrar =
      fl_plugin_registry_get_registrar_for_plugin(registry, "IscreenshotPlugin");
  iscreenshot_plugin_register_with_registrar(iscreenshot_registrar);
}
