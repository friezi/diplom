TARGET = diplomarbeit

MODULES = $(TARGET) einleitung publish_subscribe rss rss_mittels_verteiltem_pubsub adaptive_informationsverteilung das_verteilungsschema \
	verteiltes_polling koordinierung_der_subscriber bestimmung_der_intervallspanne_deltai optimierungsziel_bevorzugte_polling_periode

FILES = $(MODULES:%=%.tex)

.PHONY:	all pdf ed clean

all:	$(TARGET).dvi

$(TARGET).dvi: $(FILES)
	latex $(TARGET)
	bibtex $(TARGET)
	latex $(TARGET)
	latex $(TARGET)

pdf:	$(TARGET).pdf


$(TARGET).pdf: $(FILES)
	pdflatex $(TARGET)
#	bibtex $(TARGET)
	pdflatex $(TARGET)
	pdflatex $(TARGET)

$(TARGET)_latexdiff:
	latex $(TARGET)_latexdiff
	bibtex $(TARGET)_latexdiff
	latex $(TARGET)_latexdiff
	pdflatex $(TARGET)_latexdiff

ed:
	$(EDITOR) *.tex ../*.bib Makefile&

clean:
	\rm *.aux *.bbl *.blg *.log *.dvi *.pdf *.ps

