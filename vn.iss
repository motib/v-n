; Script generated by the Inno Setup Script Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

[Setup]
AppName=VN - Visualization of Nondeterminism
AppVerName=VN - Version 3.1.1
AppPublisher=Moti Ben-Ari, Weizmann Institute of Science
AppPublisherURL=http://stwww.weizmann.ac.il/g-cs/benari/vn/index.html
AppSupportURL=http://stwww.weizmann.ac.il/g-cs/benari/vn/index.html
AppUpdatesURL=http://stwww.weizmann.ac.il/g-cs/benari/vn/index.html
DefaultDirName={pf}\vn
DefaultGroupName=VN
OutputDir=c:\vn
AllowNoIcons=yes
LicenseFile=C:\vn\txt\gpl.txt
SetupIconFile=C:\vn\vn.ico
Compression=lzma
SolidCompression=yes

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Files]
Source: "C:\vn\bin\*"; DestDir: "{app}\bin"; Flags: ignoreversion
Source: "C:\vn\examples\*.jff"; DestDir: "{app}\examples"; Flags: ignoreversion
Source: "C:\vn\txt\*"; DestDir: "{app}\txt"; Flags: ignoreversion
Source: "C:\vn\vn\*.java"; DestDir: "{app}\vn"; Flags: ignoreversion
Source: "C:\vn\docs\vn.png"; DestDir: "{app}\docs"; Flags: ignoreversion
Source: "C:\vn\docs\vn.tex"; DestDir: "{app}\docs"; Flags: ignoreversion
Source: "C:\vn\docs\vn.pdf"; DestDir: "{app}\docs"; Flags: ignoreversion
Source: "C:\vn\vn.ico"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\vn\vn.jar"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\vn\build.bat"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\vn\config.cfg"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\vn\dist\readme.txt"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\vn\run.bat"; DestDir: "{app}"; Flags: ignoreversion
; NOTE: Don't use "Flags: ignoreversion" on any shared system files

[Icons]
Name: "{group}\VN";  Filename: "{app}\run.bat"; IconFilename: "{app}\vn.ico"; WorkingDir: "{app}"
Name: "{group}\User's Guide"; Filename: "{app}\docs\vn.pdf"
Name: "{group}\Website"; Filename: "http://stwww.weizmann.ac.il/g-cs/benari/vn/index.html"
Name: "{group}\Uninstall VN"; Filename: "{uninstallexe}"
Name: "{userdesktop}\VN"; Filename: "{app}\run.bat"; IconFilename: "{app}\vn.ico"; WorkingDir: "{app}"
