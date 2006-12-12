TARGET = diplomarbeit

MODULES = $(TARGET) einleitung grundlagen peer_to_peer publish_subscribe rss timer staukontrolle_tcp regelungstechnik rss_mittels_verteiltem_pubsub \
	adaptive_informationsverteilung das_verteilungsschema verwandte_arbeiten \
	verteiltes_polling rolle_der_broker koordinierung_der_subscriber wahl_der_zufallsspanne heuristische_bestimmung_des_ttl \
	angestrebte_dienstguete_bevorzugte_polling_periode staukontrolle feeds_und_timer ausbalancierung_der_pollingperioden churn \
	implementierung experimente zusammenfassung anhang glossar cd_rom danksagung

FILES = $(MODULES:%=%.tex)

.PHONY:	all pdf ed clean

all:	$(TARGET).dvi

$(TARGET).dvi: $(FILES)
	latex $(TARGET)
	bibtex $(TARGET)
	latex $(TARGET)
	makeindex -g -s glossar.ist -o $(TARGET).gls $(TARGET).glo
	latex $(TARGET)
	latex $(TARGET)

pdf:	$(TARGET).pdf


$(TARGET).pdf: $(FILES)
	pdflatex -shell-escape $(TARGET)
	bibtex $(TARGET)
	pdflatex -shell-escape $(TARGET)
	makeindex -g -s glossar.ist -o $(TARGET).gls $(TARGET).glo
	pdflatex -shell-escape $(TARGET)
	pdflatex -shell-escape $(TARGET)

$(TARGET)_latexdiff:
	latex $(TARGET)_latexdiff
	bibtex $(TARGET)_latexdiff
	latex $(TARGET)_latexdiff
	pdflatex $(TARGET)_latexdiff

ed:
	$(EDITOR) *.tex ../*.bib Makefile&

clean:
	\rm *.aux *.bbl *.blg *.lof *.lot *.glo *.gls *.ilg *.log *.dvi $(TARGET).ps $(TARGET).pdf

