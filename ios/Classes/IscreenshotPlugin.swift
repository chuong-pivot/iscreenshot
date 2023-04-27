import Flutter
import UIKit

public class IscreenshotPlugin: NSObject, FlutterPlugin {
	var controller :FlutterViewController!
	var messenger :FlutterBinaryMessenger
	var result :FlutterResult!
	var screenshotPath :String!


	init(controller: FlutterViewController, messenger: FlutterBinaryMessenger) {
		self.controller = controller
		self.messenger = messenger
		
		super.init()
	}
	
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "iscreenshot", binaryMessenger: registrar.messenger())

		let app = UIApplication.shared
		let controller :FlutterViewController = app.delegate!.window!!.rootViewController as! FlutterViewController

		let instance = IscreenshotPlugin(
			controller: controller,
			messenger: registrar.messenger()
		)

    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
		if call.method != "takeScreenshot" {
			result(FlutterMethodNotImplemented)
			return
		}
		
		self.result = result

		let arguments = call.arguments as! Dictionary<String, Any>
		self.screenshotPath = (arguments["saveScreenshotPath"] as! String);

		takeScreenshot(view: controller.view)
  }


//    @objc
//    func savedToGalleryDone(image: UIImage, error: NSError?, contextInfo: UnsafeMutableRawPointer?) {
//        if error == nil && self.screenshotPath != nil && !self.screenshotPath.isEmpty {
//            result(self.screenshotPath)
//        } else {
//            result(nil)
//        }
//    }
    
//    func writeImageToGallery(image :UIImage) {
//        UIImageWriteToSavedPhotosAlbum(
//            image,
//            self,
//            #selector(savedToGalleryDone),
//            nil
//        );
//    }

	func takeScreenshot(view: UIView, toImageGallery :Bool = true) {
		let scale :CGFloat = UIScreen.main.scale

		UIGraphicsBeginImageContextWithOptions(view.bounds.size, view.isOpaque, scale)

		view.drawHierarchy(in: view.bounds, afterScreenUpdates: true)
		let optionalImage :UIImage? = UIGraphicsGetImageFromCurrentImageContext()
		UIGraphicsEndImageContext()

		guard let image = optionalImage else {
				result("no image")
				return
		}

		guard let path = writeImageToPath(image: image) else {
				result("no image 1")
				return
		}

		result(path)

//        writeImageToGallery(image: image)
	}
    
//    func getScreenshotName() -> String {
//        let format = DateFormatter()
//        format.dateFormat = "yyyymmddHHmmss"
//
//        let fname :String = "native_screenshot-\(format.string(from: Date())).png"
//
//        return fname
//    }

	func getScreenshotPath() -> URL? {
		let paths :[URL] = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)

		guard let dir = paths.first else {
				return nil
		}

		return dir.appendingPathComponent( self.screenshotPath )
	}
	
	func writeImageToPath(image: UIImage) -> String? {
		guard let imageData = image.pngData() else {
				result("no image data")
				return nil
		}

		guard let path = getScreenshotPath() else {
				result("no image path")
				return nil
		}

		guard let _ = try? imageData.write(to: path) else {
				result("can't write")
				return nil
		}

		return path.path
	}
}
