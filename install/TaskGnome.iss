[Icons]
Name: {group}\Task Gnome; Filename: {app}\TaskGnome.exe; WorkingDir: {app}; IconFilename: {app}\TaskGnome.exe; IconIndex: 0
[Setup]
OutputBaseFilename=TaskGnomeSetup
VersionInfoVersion=1.1
VersionInfoCompany=-
VersionInfoProductName=Task Gnome
AppName=Task Gnome
AppVerName=Task Gnome v1.1
DefaultDirName={pf}\Task Gnome
AppendDefaultGroupName=false
VersionInfoDescription=Task Gnome
VersionInfoTextVersion=Task Gnome
VersionInfoProductVersion=1.1
AppVersion=1.1
UninstallDisplayIcon={app}\TaskGnome.exe
UninstallDisplayName=Task Gnome
UsePreviousGroup=false
DefaultGroupName=Task Gnome
WizardImageBackColor=clGreen
SetupIconFile=C:\Users\hans\workspace\JWeeklyTimeSheet\installer\install.ico
DisableProgramGroupPage=true
[Files]
Source: TaskGnome.exe; DestDir: {app}
Source: TaskGnome.ico; DestDir: {app}
Source: TaskGnome.jar; DestDir: {app}
Source: TaskGnome_lib\commons-codec-1.4.jar; DestDir: {app}\TaskGnome_lib
Source: TaskGnome_lib\h2-1.3.149.jar; DestDir: {app}\TaskGnome_lib
Source: TaskGnome_lib\JNDbm.jar; DestDir: {app}\TaskGnome_lib
Source: TaskGnome_lib\JSplitTable.jar; DestDir: {app}\TaskGnome_lib
Source: TaskGnome_lib\log4j-1.2.15.jar; DestDir: {app}\TaskGnome_lib
Source: TaskGnome_lib\miglayout-3.7.3.1.jar; DestDir: {app}\TaskGnome_lib
Source: TaskGnome_lib\swing-worker-1.2.jar; DestDir: {app}\TaskGnome_lib
Source: TaskGnome_lib\swingx-1.6.1.jar; DestDir: {app}\TaskGnome_lib
[Dirs]
Name: {app}\TaskGnome_lib
