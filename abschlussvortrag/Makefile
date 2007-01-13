TARGET=Abschlussvortrag

all:	$(TARGET).pdf

$(TARGET).pdf: $(TARGET).tex
	latex $(TARGET)
	bibtex $(TARGET)
	latex $(TARGET)
	pdflatex $(TARGET)

ed:
	$(EDITOR) *.tex ../*.bib Makefile&

clean:
	\rm *.aux *.bbl *.blg *.log *.dvi *.nav *.out *.snm *.vrb *.toc $(TARGET).pdf
