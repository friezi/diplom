TARGET = diplomarbeit

MODULES = $(TARGET) einleitung publish_subscribe rss rss_mittels_verteiltem_pubsub adaptive_informationsverteilung das_verteilungsschema \
	verteiltes_polling rolle_der_broker koordinierung_der_subscriber bestimmung_der_intervallspanne_deltai heuristische_bestimmung_des_ttl \
	optimierungsziel_bevorzugte_polling_periode staukontrolle feeds_und_timer ausbalancierung_der_pollingperioden churn anhang glossar

FILES = $(MODULES:%=%.tex)

.PHONY:	all pdf ed clean

all:	$(TARGET).dvi

$(TARGET).dvi: $(FILES)
	latex $(TARGET)
	bibtex $(TARGET)
	latex $(TARGET)
	makeindex -g -s glossar.ist -o $(TARGET).gls $(TARGET).glo
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
	\rm *.aux *.bbl *.blg *.glo *.gls *.ilg *.log *.dvi *.pdf

