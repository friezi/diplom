
all:	Thema.dvi Thema_Abstract.dvi notizen.dvi

Thema.dvi: Thema.tex
	latex Thema
	bibtex Thema
	latex Thema
	latex Thema

Thema_Abstract.dvi: Thema_Abstract.tex
	latex Thema_Abstract

pdf:	Thema.pdf Thema_Abstract.pdf

Thema.pdf: Thema.tex
	latex Thema
	bibtex Thema
	latex Thema
	pdflatex Thema

Thema_Abstract.pdf: Thema_Abstract.tex
	pdflatex Thema_Abstract

Thema_latexdiff:
	latex Thema_latexdiff
	bibtex Thema_latexdiff
	latex Thema_latexdiff
	pdflatex Thema_latexdiff

notizen.dvi: notizen.tex
	latex notizen


ed:
	emacs *.tex ../*.bib Makefile&

clean:
	\rm *.aux *.bbl *.blg *.log *.dvi *.pdf *.ps
