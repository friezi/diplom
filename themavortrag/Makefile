
all:	Vortrag.pdf

Vortrag.pdf: Vortrag.tex
	pdflatex Vortrag
	bibtex Vortrag
	pdflatex Vortrag
	pdflatex Vortrag

ed:
	$(EDITOR) *.tex ../*.bib Makefile&

clean:
	\rm *.aux *.bbl *.blg *.log *.dvi *.nav *.out *.snm *.vrb *.toc
