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


##New comments
###libs folder 
* instead of libs folder, add dependencies via build.gradle. You can use [http://gradleplease.appspot.com/](Gradle, please) in order to find libs you need

###Project structure
* I think the project structure is incorrect. The actual name of the application in Studio is 'ui'. You also have two copies of 'build' and 'gradle' folders

###package names
* it's unusual to see underscores '_' in package names
* usually package name starts with a web domain. So 'cmc' looks weird
* try to provide package names which are easy to understand. For your own ease of work in future. It's unclear to me what 'ess' means

###code organization
* it's totally up to you, but I would recommend coming up with a code style and following it everywhere. This applies for variables' names, method names, etc. I personally like to group methods/variables in a class as such: public methods go first, then private    
* this also applies to how you name variables: using underscores, camelCase, or anything other. Try to be consistent throughout the app
* also, just to keep code clean don't forget to apply private/protected to variables and methods. Try to minimize the number of public methods


###sancktory library 
* you should either create a library project or compile it into jar file. Doesn't make sense to keep under version control something you're not going to modify. If you ARE going to modify the sources, it's fine to keep them as is. 

###drawable folder
* I think it's just a mistake, but you shouldn't put sub-folders into the drawable folder

###Layouts
####activity_main.xml 
* line 8 - use wrap_content
* line 73 - I don't think you actually need 'center' property set
* line 39 - you should combine two text views into one and append the '%' sign in the format method. In case you want to use different fonts, you can apply Spans
 
###fragment_reader.xml
* I feel like you rely on RelativeLayout too much. Can't say for sure, but it seems that some parts of the layout can be simplified by using LinearLayout (no need for toLeftOf/toRightOf, etc)


###Activities

####MainActivity
* line 42 - no need for this check
* line 71 - extract all 'findView' and views initialization code into a separate method and do it only once in onCreate. Loaders can be 'reloaded' multiple times, there is no need to find views over and over again 
* line 93 - consider hiding/showing a parent view group to simplify code. No need to do that for all the particular views.
* line 93 - someView.setVisibility(cursor != null ? View.Visible : View.Gone)
* line 98/99 - use strings.xml
* line 100 - set the listener once on onCreate. Just store the path as member variable in the MainActivity and use it when button is clicked
* line 32 - nit: use private
* line 103 - use RecieverActivity.start method

###PrepareForView
* this is very unusual! :)
* rename this class to be FragmentReader or similar 
* extend this class from fragment. Override methods onCreateView (inflate xml, find views), onActivityCreated (everything is ready for display)
* move logic from RecieverActivity.mkView into this class
* make this class implement the OnSwipeTouchListener logic (instead of having a separate anonymous class)
* in the methods of OnSwipeTouchListener don't do findView over and over again. Do it once 

###RecieverActivity
Add PrepareForView (ReaderFragment) as a normal fragment. You can declare it in the xml, or add on the fly. 
The modifications you're doing to the window and decor view of the activity look suspicious. I believe it should be achieved by modifying the look of the fragment. 

In fact, as I think about it more, tt may actually be better to drop the receiver activity at all if it's transparent. Instead, you can intercept the 'shared' text in the MainActivity. 
Then you can have two fragments in the main activity: one for list of recent files, one for reading text. You can easily switch between them in runtime
 

* line 93 - this is basically called a [http://en.wikipedia.org/wiki/Abstract_factory_pattern](Factory Pattern). Nice that you come up with it, I'd just propose to extract these lines into a UtilsFactory class with a static method 'createUtil' and use it there. 
* line 138 + transparent style - it's not super clear what the point of those manipulations with the view and window... What happens if someone will share text to your app, will this activity be transparent over whatever is visible at the moment in the other app?
* line 282 - onConfigurationChanged. I don't think you really need this. If you create two xmls for the reader_fragment and put them into normal layout folder and layout-land folder - system will do most of work for you automatically
* line 293 - I don't think you need to modify the size of activity window. At least if I understand everything correctly. Instead set the dimensions of the fragment in xml
* line 324 - potentially move this method into ReaderFragment. But, don't do any database operations on the main thread (that's what happens now). Come up with a service for that. 
* line 324 - also, it might be a better idea to do this save/update in onStop to reduce the number of db operations

###ChunkData.java
I see that you only started with this idea. But chunk should actually contain the text, offset and other properties :)

###Readable.java
In my opinion, this class and it's children names could be improved. I don't think they reflect what these classes actually do

###Utils.java
Same here. Usually Utils is a representation of a collection of generic methods used by different parts of the app. 
In your case this class is more specific. But it's up to you, of course

###LastReadContentProvider.java
* Usually all db-related classes are grouped in a 'database' or similar package
* line 65 - don't do notify in query. It only applies for insert/update/delete and notifies your loaders that the data has been refreshed and needs to be re-queried from the db

###And just to remind
Don't do any file/db operations on main thread! :) Loaders are fine btw, because they are async. But inserting into the ContentResolver is not, just for example

##Books to read:
* Code Complete by Steve McConnell
* Refactoring by Martin Fowler
* Design Patterns by GoF