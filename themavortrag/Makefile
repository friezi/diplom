
all:	Vortrag.pdf

Vortrag.pdf: Vortrag.tex
	latex Vortrag
	bibtex Vortrag
	latex Vortrag
	pdflatex Vortrag

ed:
	emacs *.tex ../*.bib Makefile&

clean:
	\rm *.aux *.bbl *.blg *.log *.dvi *.nav *.out *.snm *.vrb *.toc
