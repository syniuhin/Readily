###build.gradle:
	* ? - minSdk is 13. Ideally, should be lower
	* ? - don't need this line "compile fileTree(dir: 'libs', include: ['*.jar'])"

###AndroidManifest: 
	* **FIXED** - kinda weird, why SettingsActivity is launched by default, not MainActivity	

###src folder: 
	* **FIXED** - there are not many files right now, but ideally you should group files into subfolders. For example, FileUtils & LastReadDbHelper should be placed into 'utils' or similar subfolder. 

###MainActivity: 
	* **FIXED?** - ok, I don't really understand what it does and why it's like that right now :)

###SettingsActivity:
	* **FIXED** - line 56. Toast should use text from strings.xml
	* - operations with Clipboard should ideally be placed in a ClipboardHelper or similar class
	* **FIXED** - similar, for opening a file - use a helper. Idea is that such code could be potentially reused in other places. So you should strive to extract it for easier reuse. 
	* **FIXED** - use 'MimeTypeMap.getFileExtensionFromUrl(myfile.toURL().toString())' to check file extension - it's more robust. There might be files with no '.' in file name - you'd get a crash in such case
	* - Never do any database, file or network operation on main thread. I suggest taking a look at 'http://developer.android.com/reference/android/os/StrictMode.html' to control yourself. 
	In this particular case, if you encounter a file which is 200 mb in size, youre app is going to freeze for some time while it reads the file. 
	 You should put the code which reads the file into a service or an AsyncTask, although AsyncTasks are generally considered bad and should be avoided. 
	 One more thing to think about here is that if somebody would give you a file with a size of 200MB, the app is also going to crash, this time because of OutOfMemory exception. I would consider reading it in chunks. Plus, passing such a huge string to another activity is also not the best idea - it's very slow. I would either encapsulate this functionality in some generic class, or, even more preferably - a service
	* - 'updateSpeedGrayness' method should be private
	* **FIXED** - 'startReceiverActivity' should actually be a public static member of the RecieverActivity

###ReceiverActivity:
    	* - you should never have a function which doesn't fit one screen (when you have to scroll to see the whole function body). The 'onCreate' method should be broken down into smaller pieces 
	* - I don't think you need this call 'requestWindowFeature(Window.FEATURE_ACTION_BAR);' on sdk v13 and up
