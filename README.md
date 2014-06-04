FREVAL
======

Software related to the paper "All Fragments Count in Parser Evaluation"

Abstract
--------

PARSEVAL, the default paradigm for evaluating constituency parsers, calculates parsing success (Precision/Recall) as a function of the number of matching labeled brackets across the test set. Nodes in constituency trees, however, are connected together to reflect important linguistic relations such as predicate-argument and direct-dominance relations between categories. In this paper, we present FREVAL, a generalization of PARSEVAL, where the precision and recall are calculated not only for individual brackets, but also for co-occurring, connected brackets (i.e. fragments). FREVAL fragments precision (FLP) and recall (FLR) interpolate the match across the whole spectrum of fragment sizes ranging from those consisting of individual nodes (labeled brackets) to those consisting of full parse trees. We provide evidence that FREVAL is informative for inspecting relative parser performance by comparing a range of existing parsers.


If you use this software in scientific work, then please cite:
--------------------------------------------------------------

Bibtex	@InProceedings{BASTINGS14.376,
  author = {Joost Bastings and Khalil Sima'an},
  title = {All Fragments Count in Parser Evaluation},
  booktitle = {Proceedings of the Ninth International Conference on Language Resources and Evaluation (LREC'14)},
  year = {2014},
  month = {may},
  date = {26-31},
  address = {Reykjavik, Iceland},
  editor = {Nicoletta Calzolari (Conference Chair) and Khalid Choukri and Thierry Declerck and Hrafn Loftsson and Bente Maegaard and Joseph Mariani and Asuncion Moreno and Jan Odijk and Stelios Piperidis},
  publisher = {European Language Resources Association (ELRA)},
  isbn = {978-2-9517408-8-4},
  language = {english}
 }

Instructions
------------

1. To run FREVAL, copy run-sample.sh to a new file and edit the arguments.
2. If you want to run FREVAL with e.g. a certain fragment size limit, then edit the properties in the properties folder.
3. Please note that the sample may take a considerable time to run with the current sample data (EVALBs sample data).
