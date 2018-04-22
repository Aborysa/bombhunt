\ProvidesClass{GLO_NTNU}[2018/01/15 Customized Document Class for NTNU Assignments]
\LoadClassWithOptions{report}

%Package Loading
\RequirePackage[utf8]{inputenc}
\RequirePackage[T1]{fontenc}
\RequirePackage{lmodern}
\RequirePackage[english]{babel}
\RequirePackage{bibentry}
\RequirePackage{natbib}
\RequirePackage[top=25mm, bottom=25mm, left=25mm, right=25mm]{geometry}
\RequirePackage{amssymb}
\RequirePackage{amsmath}
\RequirePackage{titlepic}
\RequirePackage{graphicx}
\RequirePackage{color}
\RequirePackage[dvipsnames,table]{xcolor}
\RequirePackage{tikz}
\usetikzlibrary{positioning,calc,arrows,shapes,snakes,automata,backgrounds,petri}
\RequirePackage{ifthen}
\RequirePackage{textpos}
\RequirePackage{calc}
\RequirePackage{tabularx}
\RequirePackage{booktabs}
\RequirePackage{xstring}
\RequirePackage{makecell}
\RequirePackage[colorlinks=true,allcolors=blue]{hyperref}
\RequirePackage{subfig}
\RequirePackage{pifont}
\RequirePackage[bottom]{footmisc}
%\RequirePackage{MnSymbol} %interference with amssymb http://ctan.mirror.rafal.ca/fonts/mnsymbol/MnSymbol.pdf
\RequirePackage{mathdots}
\RequirePackage{mathtools}
\RequirePackage{array}
\RequirePackage{multicol}
\RequirePackage{multirow}

\RequirePackage{nomencl}
\makenomenclature
%\renewcommand{\nomname}{Liste des symboles et abréviations}
% to be used with: makeindex main.nlo -s nomencl.ist -o main.nls
% numberedsection: add this option to make the section numbered (useful if moved in appendix)
\RequirePackage[nonumberlist,acronym,toc,style=long]{glossaries}
\setlength\LTleft{0pt}
\setlength\LTright{0pt}
\setlength\glsdescwidth{0.8\hsize}
\renewcommand{\glsnamefont}[1]{\textbf{#1}}
\makeglossaries
\loadglsentries{gls/glossary}
\glsaddall

\RequirePackage{enumitem}
\RequirePackage{changepage}
\RequirePackage{etoolbox}
\RequirePackage[section]{placeins}
\RequirePackage{karnaughmap}

\RequirePackage{minted}
\RequirePackage[babel]{csquotes}

% \RequiredPackage{appendix} leads to an error...?
\usepackage[page,toc,titletoc,title]{appendix}
\newcommand*{\Appendixautorefname}{Appendix}

% \RequiredPackage{lscape} leads to an error...?
\usepackage{lscape}

\usepackage{listings}
\def\listingautorefname{Listing}
\renewcommand{\listoflistings}{\begingroup
\listof{listing}{\listoflistingscaption}
\addcontentsline{toc}{chapter}{\listoflistingscaption}
\endgroup}
\newenvironment{longlisting}{\captionsetup{type=listing}}{}


%Code Generating the Title Page
\newif\ifbTeam
\def\bool_Team{\bTeamtrue}
\def\TeamNumber#1{\def\theTeamNumber{#1}}
\def\theTeamNumber{Team Number}
\def\TeamName#1{\def\theTeamName{#1}}
\def\theTeamName{Team Name}
\def\Author#1{\def\theAuthor{#1}}
\def\theAuthor{Cabral Cruz, Samuel \\ (111 006 369)}
\def\Course#1{\def\theCourse{#1}}
\def\theCourse{Default Course Title \\ Default Course Number XXX0000}
\def\ProjectTitle#1{\def\theProjectTitle{#1}}
\def\theProjectTitle{Default Project Title}
\def\ReportTitle#1{\def\theReportTitle{#1}}
\def\theReportTitle{Default Document Title}
\def\COTS#1{\def\theCOTS{#1}}
\def\theCOTS{Commercial off-the-shell}
\def\PQA#1{\def\thePQA{#1}}
\def\thePQA{Primary Quality Attribute}
\def\SQA#1{\def\theSQA{#1}}
\def\theSQA{Secondary Quality Attribute(s)}
\def\Teacher#1{\def\theTeacher{#1}}
\def\theTeacher{Default Lecturer Name}
\def\Department#1{\def\theDepartment{#1}}
\def\theDepartment{Default Department Name}
\def\TrimesterYear#1{\def\theTrimesterYear{#1}}
\def\theTrimesterYear{Default Semester and Year}
\def\UniversityLocation#1{\def\theUniversityLocation{#1}}
\def\theUniversityLocation{Default University Location}
\newcounter{Nb_Teamates}
\setcounter{Nb_Teamates}{1}
\renewcommand{\and}{
	\\[\baselineskip]
	\stepcounter{Nb_Teamates}}

% Get the document class path
\makeatletter
\def\thepwd@default{./}
\let\thepwd\thepwd@default
\let\theclasspath\@empty

\newcommand\getclasspath{%
    \begingroup
    \edef\filename{GLO_NTNU.cls}%
    \@onelevel@sanitize\filename%
    \let\thepwd\thepwd@default
    \let\theclasspath\@empty
    \IfFileExists{\jobname.fls}{%
        \openin\@inputcheck=\jobname.fls\relax
        \endlinechar\m@ne
        \readline\@inputcheck to \line
        \expandafter\getclasspath@extr\line\relax\relax\relax\relax\relax
        \expandafter\getclasspath@defs\expandafter{\filename}%
        \loop
            \readline\@inputcheck to \line
            \@onelevel@sanitize\line
            \expandafter\getclasspath@path\expandafter{\line}%
            \ifeof\@inputcheck
                \let\iterate\relax
            \fi
            \ifx\theclasspath\@empty
        \repeat
        \closein\@inputcheck
    }{%
        \PackageWarning{getclasspath}
            {The required recorder file (.fls) was not found.\MessageBreak
             Please compile with the '-recorder' option.\MessageBreak
             Occurred}%
    }%
    \ifx\theclasspath\@empty
        \let\theclasspath\thepwd
    \fi
    \edef\@tempa{%
        \def\noexpand\thepwd{\thepwd}%
        \def\noexpand\theclasspath{\theclasspath}%
    }%
    \expandafter
    \endgroup
    \@tempa
}
\def\getclasspath@extr#1#2#3#4#5\relax{%
    \edef\@tempa{\detokenize{#1#2#3}}%
    \edef\@tempb{\detokenize{PWD}}%
    \ifx\@tempa\@tempb
       \edef\thepwd{\detokenize{#4#5/}}%
    \fi
}

\begingroup
\catcode`I=12
\catcode`N=12
\catcode`P=12
\catcode`U=12
\catcode`T=12
\gdef\getclasspath@defs#1{%
    \def\getclasspath@@path ##1INPUT ##2#1\relax##3\relax##4\@nnil{%
        \ifx\@empty##4\@empty\else
            \def\theclasspath{##2}%
        \fi
    }%
    \def\getclasspath@path##1{%
        \getclasspath@@path##1\relax INPUT \@empty#1\relax{}\relax\@nnil
    }%
}
\endgroup
\makeatother
\getclasspath
\message{ClassPath: \theclasspath}
\def\figureFolder{fig}
\message{GraphicsPathDocumentClass: \theclasspath\figureFolder/}
\graphicspath{{\theclasspath\figureFolder/}{./\figureFolder/}{.}}

\renewcommand{\maketitle}{
	\begin{titlepage}
		\begin{flushleft}
			\includegraphics[width=0.25\textwidth, height=0.25\textheight,keepaspectratio]{NTNU_logo_abbrev}\\
			\ifbTeam
				\vspace{-30pt}
				\raggedleft \textbf{\theTeamName} \\
				\raggedleft Team Number: \theTeamNumber
			\fi
		\end{flushleft}
		\vfill
		\begin{center}
			\scshape
			\large\theCourse \\
			\vspace{2mm}
			\begin{minipage}{0.8\linewidth}
				\centering
				\hrule
				\vspace{5mm}
				\textbf{
					\LARGE{\theProjectTitle} \\
					\Large{\theReportTitle} \\
					\Large{\theCOTS}}
				\vspace{5mm}
				\hrule
			\end{minipage}
			\vfill
			\normalfont\small\textit{\theAuthor}\normalfont\scshape \\
			\vfill
			\textbf{Primary Quality Attribute:} \\ \thePQA \\
			\textbf{Secondary Quality Attribute(s):} \\ \theSQA \\
			\vfill
			Presented to \\
			\theTeacher
			\vfill
			Department \theDepartment \\
			Norwegian University of Science and Technology \\
			(NTNU), \theUniversityLocation \\
			\theTrimesterYear
		\end{center}
	\end{titlepage}}

% Default Addition of Pictures
\newcommand{\AddPicture}[5][nolabel]{
	\begin{figure}[ht]
		\begin{center}
			\includegraphics[width=#2\textwidth, height=#3\textheight,keepaspectratio]{#4}
			\caption{#5}
			\ifthenelse{\equal{#1}{nolabel}}{}{\label{fig:#1}}
		\end{center}
	\end{figure}}

\newcommand{\AddPictureLandscape}[5][nolabel]{
    \begin{landscape}
      \begin{figure}
        \includegraphics[width=#2\hsize, height=#3\vsize]{#4}
        \caption{#5}
        \label{fig:#1}
      \end{figure}
    \end{landscape}}

% Default Answering to QA assignment
\newcommand{\QA}[2]{
  \hspace{\parindent}\textbf{Statement}
  \begin{adjustwidth}{2\parindent}{0pt}
    #1
  \end{adjustwidth}
  \vspace{\baselineskip}
  \hspace{\parindent}\textbf{Answer}
  \begin{adjustwidth}{2\parindent}{0pt}
    #2
  \end{adjustwidth}}

% Running Terminal Command and Outputing Standard Output to LaTeX
\begingroup\makeatletter\endlinechar=\m@ne\everyeof{\noexpand}
\edef\x{\endgroup\def\noexpand\TeXpath{\@@input|"which tex " }}\x
\newcommand{\RunTerminal}[1]{
	\begin{minipage}{\linewidth}
		{\catcode`_=12 \ttfamily\input{|"#1"}}
	\end{minipage}}

%Redifining the icons of itemize
\newcommand{\modifyItemize}{
	\renewcommand{\labelitemi}{\tiny $\blacktriangleright$}
	\renewcommand{\labelitemii}{\footnotesize $\bullet$}
	\renewcommand{\labelitemiii}{\tiny \ding{117}}
	\renewcommand{\labelitemiv}{\small $\star$}}

%Defining new column type for table environments
\newcolumntype{M}[1]{>{\centering\arraybackslash}m{#1}}
\newcolumntype{L}[1]{>{\raggedright\let\newline\\\arraybackslash\hspace{0pt}}m{#1}}
\newcolumntype{Y}{>{\centering\arraybackslash}X}
\renewcommand\theadfont{\bfseries}

%Commenting equations
\newcommand{\comments}[1]{
    \hfill \langle \text{#1} \rangle}

%XOR operator
\newcommand*\xor{\mathbin{\oplus}}

%QED symbol
\newcommand*{\QED}{\hfill\ensuremath{\square}}

%Full Ref for unnumbered chapters
\newcommand{\fullref}[1]{\hyperref[#1]{\autoref*{#1}\nameref*{#1}}}

%Add unnumbered chapter to toc (create label opt)
\newcommand{\unnumberedChapter}[2][nolabel]{
	\chapter*{#2}
	\addcontentsline{toc}{chapter}{#2}
	\label{chap:#1}}

%Add unnumbered section to toc (create label opt)
\newcommand{\unnumberedSection}[2][nolabel]{
	\section*{#2}
	\addcontentsline{toc}{section}{#2}
	\label{sec:#1}}

%Add unnumbered subsection to toc (create label opt)
\newcommand{\unnumberedSubsection}[2][nolabel]{
	\subsection*{#2}
	\addcontentsline{toc}{subsection}{#2}
	\label{subsec:#1}}

%Specific command
\newcommand{\QAReq}[9]{
	% #1 ID
	% #2 Caption
	% #3 Label
	% #4 Source
	% #5 Stimulus
	% #6 Artifacts
	% #7 Environment
	% #8 Response
	% #9 Response measure
	\begin{table}[ht]
		\centering
		\begin{tabularx}{\textwidth}{>{\bfseries}p{5cm}X}
			\toprule
				ID & \textbf{#1} \\
			\midrule
				Source & #4 \\
				Stimulus & #5 \\
				Artifacts & #6 \\
				Environment & #7 \\
				Response & #8 \\
				Response measure & #9 \\
			\bottomrule
		\end{tabularx}
		\caption{#2}
		\label{tab:#1}
	\end{table}}

\newcommand{\ATAM}[9]{
	% #1 ID
	% #2 Caption
	% #3 Attribute(s)
	% #4 Stimulus
	% #5 Response/Response measure
	% #6 Analysis Table
	% #7 Reasoning
	% #8 Architectural Diagram
	% Label = ATAM || ID
	\begin{table}[ht]
		\centering
		\begin{tabular}{p{4cm}p{2.5cm}p{2.5cm}p{2.5cm}p{2.5cm}}
			\toprule
				\textbf{ID:} #1 & \multicolumn{4}{l}{\textbf{Scenario:} #2} \\
			\midrule
				\textbf{Attribute(s):} & \multicolumn{4}{l}{#3} \\
				\textbf{Stimulus:} & \multicolumn{4}{l}{#4} \\
				\textbf{Response:} & \multicolumn{4}{L{12cm}}{#5} \\
			\midrule
			\textbf{Architectural \newline Decisions} & \textbf{Sensitivity} & \textbf{Tradeoff} & \textbf{Risk} & \textbf{Non-risk} \\
			\midrule
			#6
			\midrule
			\multicolumn{5}{L{15cm}}{\textbf{Reasoning:} \newline #7} \\
			\multicolumn{5}{L{15cm}}{\textbf{Architectural \newline Diagram:} #8} \\
			\bottomrule
		\end{tabular}
		\caption{#2}
		\label{tab:ATAM#1}
	\end{table}
}
