; THIS IS A SAMPLE FOR UPDATE CHECKER
; You need modify them to fit your game.
Global UpdateCheckEnabled% = GetINIInt(OptionFile, "options", "check for updates")
Global UpdaterBG

Type ChangeLogLines
	Field txt$
End Type

Global UpdaterIMG
Global LinesAmount% = 0

Function CheckForUpdates%()
	AppTitle "SCP - Containment Breach Updater"
	
	If !UpdateCheckEnabled Then Return 0
	
	SetBuffer BackBuffer()
	Cls
	Color 255,255,255
	Text 320,240,"Checking for updates...",True,True
	Flip
	
	Local domainTXT$ = GetDomainTXT("version.scpcbgame.cn") ; this domain is for SCPCB chinese
	Local versionTXT$ = ParseDomainTXT(domainTXT, "version") ; get key of section "version"
	Local dateTXT$ = ParseDomainTXT(domainTXT, "date")
	DebugLog domainTXT
	If versionTXT = "" Then 
		DebugLog "Get TXT failed!"
		;Return -1
	EndIf

	If versionTXT != VersionNumber Then ;diffirent with game version...
		DebugLog "Newer version!"
		DownloadFile("https://filesamples.com/samples/document/txt/sample1.txt", "changelog_website.txt") ;download changelog(this is a sample file)
		Local ChangeLogFile% = ReadFile("changelog_website.txt") ;读取文件
		
		UpdaterBG = LoadImage_Strict("GFX\menu\updater.jpg")
		UpdaterIMG = CreateImage(452,254)
		
		Local ChangeLogLineAmount% = 0
		Local FirstLine% = True
		If ChangeLogFile != 0 Then
			While Not Eof(ChangeLogFile)
				l$ = ReadLine(ChangeLogFile)
				If Left(l, 5) != "-----"
					chl.ChangeLogLines = New ChangeLogLines
					If FirstLine Then
						chl\txt$ = "NEW UPDATE: "+l
						FirstLine = False
					Else
						chl\txt$ = l
					EndIf
					ChangeLogLineAmount = ChangeLogLineAmount + 1
				Else
					Exit
				EndIf
			Wend
			CloseFile(ChangeLogFile)
			DeleteFile("changelog_website.txt")
		Else
			chl.ChangeLogLines = New ChangeLogLines
			chl\txt$ = "Changelog download failed."
			chl.ChangeLogLines = New ChangeLogLines
			chl\txt$ = "Latest Version："+versionTXT
			chl.ChangeLogLines = New ChangeLogLines
			chl\txt$ = "Update Date："+dateTXT
			chl.ChangeLogLines = New ChangeLogLines
			chl\txt$ = "Changelog link：https://scpcbgame.cn/changelog.txt"
		EndIf
		UpdaterFont = LoadFont_Strict("GFX\font\cour\Courier New.ttf",16)
		
		Repeat
			SetBuffer BackBuffer()
			Cls
			Color 255,255,255
			MouseHit1 = MouseHit(1)
			MouseDown1 = MouseDown(1)
			DrawImage UpdaterBG,0,0
			
			SetFont UpdaterFont
			If LinesAmount > 13
				y# = 200-(20*ScrollMenuHeight*ScrollBarY)
				LinesAmount% = 0
				SetBuffer(ImageBuffer(UpdaterIMG))
				DrawImage UpdaterBG,-20,-195
				For chl.ChangeLogLines = Each ChangeLogLines
					Color 1,0,0
					If Left(chl\txt$,10) = "NEW UPDATE" Then Color 200,0,0
					If chl\txt$ = "Changelog download failed." Then Color 255,0,0
					RowText(chl\txt$,2,y#-195,430,254)
					y# = y#+(20*GetLineAmount2(chl\txt$,432,254))
					LinesAmount = LinesAmount + (GetLineAmount2(chl\txt$,432,254))
				Next
				SetBuffer BackBuffer()
				DrawImage UpdaterIMG,20,195
				Color 10,10,10
				Rect 452,195,20,254,True
				ScrollMenuHeight# = LinesAmount-12.3
				ScrollBarY = DrawScrollBar(452,195,20,254,452,195+(254-(254-4*ScrollMenuHeight))*ScrollBarY,20,254-(4*ScrollMenuHeight),ScrollBarY,1)
			Else
				y# = 201
				LinesAmount% = 0
				For chl.ChangeLogLines = Each ChangeLogLines
					Color 1,0,0
					If Left(chl\txt$,10) = "NEW UPDATE" Then Color 200,0,0
					If chl\txt$ = "Changelog download failed." Then Color 255,0,0
					RowText(chl\txt$,21,y#,431,253)
					y# = y#+(20*GetLineAmount2(chl\txt$,432,254))
					LinesAmount = LinesAmount + (GetLineAmount2(chl\txt$,432,254))
				Next
				ScrollMenuHeight# = LinesAmount
			EndIf
			Color 255,255,255
			Rect 480, 200, 140, 95
			Color 0,0,0
			RowText2("Current Version: "+VersionNumber,482,210,137,90)
			
			SetFont Font1
			If DrawButton(LauncherWidth - 30 - 90 - 20, LauncherHeight - 65 - 100, 100, 30, "RETRY", False, False, False)
				Delete Each ChangeLogLines
				If UpdaterIMG != 0 Then FreeImage UpdaterIMG
				CheckForUpdates()
				Return 0
			EndIf
			If DrawButton(LauncherWidth - 30 - 90 - 20, LauncherHeight - 65 - 50, 100, 30, "DOWNLOAD", False, False, False)
				ExecFile("https://scpcbgame.com")
				Delay 100
				End
			EndIf
			If DrawButton(LauncherWidth - 30 - 90 - 20, LauncherHeight - 65, 100, 30, "INGORE", False, False, False)
				Delay 100
				Exit
			EndIf
			
			Flip
			Delay 8
		Forever
	Else 
		DebugLog "No newer version!"
	EndIf
	Delete Each ChangeLogLines
	If UpdaterIMG != 0 Then FreeImage UpdaterIMG
	Return 0
End Function