# EventSpotter - spotting entities that talk about events

[EventSpotter][EventSpotter] is the event entity identification and disambiguation system created by the Multimedia Communication Department at [Institut Eurecom]. It identifies occurences of named musical event entities in unstructured text. Most event names are ambiguous, and Eventspotter takes into consideration this ambiguity. 

If you want to be notified about  news or new releases, send us a mail to:

	Amar.Kadamalakunte@eurecom.fr
	Meghana.Sreekanta-Murthy@eurecom.fr
    Raphael.Troncy@eurecom.fr
	Giuseppe.Rizzo@eurecom.fr

## Introduction to EventSpotter

EventSpotter is currently a framework for musical event detection and disambiguation. Given free form text, it maps occurences of musical event titles onto musical entities registered in the EventMedia dataset. EventSpotter is useful for extracting knowledge about the musical entities, such as artist, date, location, category as well.
EventMedia is a huge hub of social event data obtained from three public event directories: Last.fm, Eventful and Upcoming and the media directory: Flickr. We use a snapshot of the EventMedia dataset, available at[https://github.com/amark-india/eventspotter/tree/master/data/EventMedia_2012.csv].
EventTitles.list file can be considered an event indexer or lexicon which contains, as the name suggests, titles of all events contained in EventMedia mapped to their respective event id.

## Requirements

EventSpotter was written in Java, and requires Java 6. EventSpotter also needs a MySQL[https://github.com/amark-india/eventspotter/tree/master/data/eventspotterdb] database to run.
EventSpotter uses a preprocessed snapshot of the EventMedia dataset found at [EventMedia_2012preprocessed.csv]. 
The machine that EventSpotter runs on should have a reasonable amount of main memory since we load the eventTitles.list file onto the memory when eventspotter file is executed.
Since the snapshot of the EventMedia dataset we used contains more than 65K entries, it is important to have sufficient main memory for smooth execution.

## Setting up the Eventspotterdb repository

EventSpotter was developed to disambiguate event entities using the EventMedia knowledge base, returning the EventMedia identifier for disambiguated entities. 
However, you can use EventSpotter with any entity repository, given that you have aritists and event descriptions for all entities. The more common case is to use EventSpotter with EventMedia. 
If you want to set it up with your own repository, see the Advanced Configuration section.

To use EventSpotter with EventMedia database , download the  EventMedia repository we provide on our [github repository] as a csv dump and import it into your database server or you can use the database file .
Once the import is done, you can start using EventSpotter immediately by adjusting the `/src/fr/eurecom/eventspotter/EventSpotter.java` file at line 36 to `this.dbPath = "jdbc:mysql://localhost/YourDataBaseName;` to point to the database.
EventSpotter will then use nearly 65K named event ntities harvested from Last.fm, Upcoming and Eventful for disambiguation.

Get the Entity Repository (24 GB):

		https://github.com/amark-india/eventspotter/archive/master.zip
		
Import it into a postgres database:

		  mysql -u user --password=password <DATABASE> < eventspotterdb.sql

    
where <DATABASE> is a database on a MySQL server.

## Setting up EventSpotter

To build EventSpotter, unzip eventspotter-master.zip and import the eventspotter-master into eclipse as an existing java project.
Add the libraries in the lib folder to the build path and export Main.java into a jar file. Refer to this for jar file creation in eclipse [http://help.eclipse.org/juno/index.jsp?topic=%2Forg.eclipse.jdt.doc.user%2Ftasks%2Ftasks-33.htm]
This will create an EventSpotter.jar including all dependencies.

## Hands-On Example

The main classes in EventSpotter are `mpi.EventSpotter.Preparator` for preparing an input document and `mpi.EventSpotter.Disambiguator` for running the disambiguation on the prepared input.

	// Define the input.
	String inputText = "When [[Page]] played Kashmir at Knebworth, his Les Paul was uniquely tuned.";
	
	// Prepare the input for disambiguation. The Stanford NER will be run
	// to identify names. Strings marked with [[ ]] will also be treated as names.
	PreparationSettings prepSettings = new StanfordHybridPreparationSettings();
	Preparator p = new Preparator();
	PreparedInput input = p.prepare(inputText, prepSettings);
	
	// Disambiguate the input with the graph coherence algorithm.
	DisambiguationSettings disSettings = new CocktailPartyDisambiguationSettings();    
	Disambiguator d = new Disambiguator(input, disSettings);
	DisambiguationResults results = d.disambiguate();
	
	// Print the disambiguation results.
	for (ResultMention rm : results.getResultMentions()) {
      ResultEntity re = results.getBestEntity(rm);
	  System.out.println(rm.getMention() + " -> " + re +
      " (" + EventSpotterManager.getWikipediaUrl(re) + ")");
	}
	
The `ResultEntity` contains the EventSpotter ID via the `getEntity()` method. This can be transformed into a Wikipedia URL by calling `EventSpotterManager.getWikipediaUrl()` for the result entity.

See the `mpi.EventSpotter.config.settings.disambiguation` package for all possible predefined configurations, passed to the `Disambiguator`:

* `PriorOnlyDisambiguationSettings`: Annotate each mention with the most prominent entity.
* `LocalDisambiguationSettings`: Use the entity prominence and the keyphrase-context similarity to disambiguate.
* `CocktailPartyDisambiguationSettings`: Use a graph algorithm on the entity coherence graph ([MilneWitten] link coherence) to disambiguate. 
* `CocktailPartyKOREDisambiguationSettings`: Use a graph algorithm on the entity coherence graph ([KORE] link coherence) to disambiguate. 

## Hands-On Command Line Call Example

1. Build EventSpotter:

    `ant`
    
1. Run the CommandLineDisambiguator:

    `java -Xmx4G -cp EventSpotter.jar -I <INPUT-FILE> -O <OUTPUT-FILE>

`<INPUT-FILE>` is path to the text file to be annotated with entities. The format for `<INPUT-FILE>` should be plain text with UTF-8 encoding.
`<OUTPUT-FILE>` is path to the text file which will contain all the event entity spots in JSON format along with a confidence score for each spot.
Adding the -E <minus eval> option to generate a conll file for evaluation. 

The output will be displayed on the console. The EventSpotter displays an in-place replacement of event entities with <EVENT>entity</EVENT> tags.
It also displays the details of each spot in JSON format along with the [http://data.linkedevents.org/] URL for each event. This JSON output is also written into the output file .

## Input Format

The input of EventSpotter is an English language text (as Java String) or file in UTF-8 encoding. By default, named entities are recognized by the Stanford NER component of the [CoreNLP][CoreNLP] tool suite. In addition, mentions can be marked up by square brackets, as in this example "Page":

    When [[Page]] played Kashmir at Knebworth, his Les Paul was uniquely tuned.
    
The mention recognition can be configured by using different `PreparationSettings` in the `mpi.EventSpotter.config.settings.preparation` package:

* `StanfordHybridPreparationSettings`: Use Stanford CoreNLP NER and allow manual markup using [[...]]
* `StanfordManualPreparationSettings`: Use Stanford CoreNLP only for tokenization and sentence splitting, mentions need to be marked up by [[...]].

The `PreparationSettings` are passed to the `Preparator`, see the Hands-On API Example.

## Advanced Configuration

### Configuring the DisambiguationSettings

The `mpi.EventSpotter.config.settings.DisambiguationSettings` contain all the configurations for the weight computation of the disambiguation graph. The best way to configure the DisambiguationSettings for constructing the disambiguation graph is to use one of the predefined settings objects in the `mpi.EventSpotter.config.settings.disambiguation` package, see below.

### Pre-configured DisambiguationSettings

These pre-configured `DisambiguatorSettings` objects can be passed to the `Disambiguator`:

* `PriorOnlyDisambiguationSettings`: Annotate each mention with the most prominent entity.
* `LocalDisambiguationSettings`: Use the entity prominence and the keyphrase-context similarity to disambiguate.
* `CocktailPartyDisambiguationSettings`: Use a graph algorithm on the entity coherence graph ([MilneWitten] link coherence) to disambiguate. 
* `CocktailPartyKOREDisambiguationSettings`: Use a graph algorithm on the entity coherence graph ([KORE] link coherence) to disambiguate. 

#### DisambiguationSettings Parameters

The principle parameters are (corresponding to all the instance variables of the `DisambiguationSettings` object):

* `alpha`: Balances the mention-entity edge weights (alpha) and the entity-entity edge weights (1-alpha).
* `disambiguationTechnique`: Technique to solve the disambiguation graph with. Most commonly this is LOCAL for mention-entity similarity edges only and GRAPH to include the entity coherence.
* `disambiguationAlgorithm`: If TECHNIQUE.GRAPH is chosen above, this specifies the algorithm to solve the disambiguation graph. Can be COCKTAIL_PARTY for the full disambiguation graph and COCKTAIL_PARTY_SIZE_CONSTRAINED for a heuristically pruned graph.
* `useExhaustiveSearch`: Set to true to use exhaustive search in the final solving stage of ALGORITHM.COCKTAIL_PARTY. Set to false to do a hill-climbing search from a random starting point.
* `useNormalizedObjective`: Set to true to normalize the minimum weighted degree in the ALGORITHM.COCKTAIL_PARTY by the number of graph nodes. This prefers smaller solutions.
* `entitiesPerMentionConstraint`: Number of candidates to keep for for ALGORITHM.COCKTAIL_PARTY_SIZE_CONSTRAINED.
* `useCoherenceRobustnessTest`: Set to true to enable the coherence robustness test, fixing mentions with highly similar prior and similarity distribution to the most promising candidate before running the graph algorithm.
* `cohRobustnessThreshold`: Threshold of the robustness test, below which the the L1-norm between prior and sim results in the fixing of the entity candidate.
* `similaritySettings`: Settings to compute the edge-weights of the disambiguation graph. Details see below.
* `coherenceSimilaritySetting`: Settings to compute the initial mention-entity edge weights when using coherence robustness.

The edge weights of the disambiguation graph are configured in the `similaritySettings` object of `DisambiguationSettings`. They have a major impact on the outcome of the disambiguation.

#### SimilaritySettings Parameters

* `mentionEntitySimilarities`: a list of mention-entity similarity triples. The first one is the SimilarityMeasure, the second the EntitiesContext, the third the weight of this mentionEntitySimilarity. Note that they need to add up to 1.0, including the number for the priorWeight option. If loading from a file, the triples are separated by ":". The mentionEntitySimilarities option also allows to enable or disable the first or second half of the mention-entity similarities based on the priorThreshold option. If this is present, the first half of the list is used when the prior is disable, the second one when it is enabled. Note that still the whole list weights need to sum up to 1 with the prior, the EnsembleMentionEntitySimilarity class will take care of appropriate re-scaling.
* `priorWeight`: The weight of the prior probability. Needs to sum up to 1.0 with all weights in mentionEntitySimilarities.
* `priorThreshold`: If set, the first half of mentionEntitySimilarities will be used for the mention-entity similarity when the best prior for an entity candidate is below the given threshold, otherwise the second half of the list together with the prior is used.
* `entityEntitySimilarity`: The name and the weight of the entity-entity similarity to use, as pairs of name and weight. If loading from a file, the pairs are ":"-separated.

Take our default configuration as example (in File syntax):

    mentionEntitySimilarities = UnnormalizedKeyphrasesBasedMISimilarity:KeyphrasesContext:1.4616111666431395E-5 UnnormalizedKeyphrasesBasedIDFSimilarity:KeyphrasesContext:4.291375037765039E-5 UnnormalizedKeyphrasesBasedMISimilarity:KeyphrasesContext:0.15586170799823845 UnnormalizedKeyphrasesBasedIDFSimilarity:KeyphrasesContext:0.645200419577534
    priorWeight = 0.19888034256218348
    priorThreshold = 0.9
    entityEntitySimilarity = MilneWittenEntityEntitySimilarity:1.0

It is possible to create a SimilaritySettings object programmatically, however we recommend using the preconfigured settings in the `mpi.EventSpotter.config.settings.disambiguation` package.

### Adjusting the StopWords

If you want to add your own stopwords, you can add them to `settings/tokens/stopwords6.txt`.

### Using EventSpotter with your own Entity Repository

You can deploy EventSpotter with any set of named entities, given that you have descriptive keyphrases and weights for them. The database layout has to conform to the one described here. For a good example instance of all the data please download the YAGO2-based EventSpotter entity repository from our website.

#### Database Tables
    
The mandatory database tables are:

* dictionary
* entity_ids
* entity_keyphrases
* keyword_counts
* word_ids
* word_expansion

Each one is described in detail below, starting with the table name plus column names and SQL types.
    
    dictionary (
      mention text, entity integer, prior double precision
    )
    
The _dictionary_ is used for looking up _entity_ candidates for a given surface form of a _mention_. Each mention-entity pair can have an associated prior probability. Mentions with the length of 4 characters or more are case-conflated to all-upper case. Also, mentions are normalized using the YAGO2 basics.Normalize.string() method (included as a jar.). To get the original mentoin string, use basics.Normalize.unString().
    
    entity_ids (
      entity text, id integer
    )
    
This table is used for mapping the integer ids to a human-readable entity representation. In the existing repository, entities are encoded using the basics.Normalize.entity() method. To get the original entity name (as taken from Wikipedia), use basics.Normalize.unEntity().

    keyword_counts (
      keyword integer, count integer
    )
    
The counts should reflect the number of times the given keyword occurs in the collection and is used to compute the IDF weight for all keywords. This means high counts will result in low weights.
    
    word_ids (
      word text, id integer
    )
    
All keyphrase and keyword ids must be present here. The input text will be transformed using the table and then matched against all entity keyphrases.
    
    word_expansion (
      word integer, expansion integer
    )
    
EventSpotter tries to match ALL_CAPS variants of mixed-case keywords. Put the ids of the UPPER_CASED word it in this table.

    entity_keyphrases (
      entity integer, keyphrase integer, keyphrase_tokens integer[], source character varying(100), count integer, weight double precision DEFAULT 1.0, keyphrase_token_weights double precision[]
    )
    
This is the meat of EventSpotter. All entities are associated with (optionally weighted) keyphrases, represented by an integer id. As the keyphrases are matched partially against input text, the (weighted) _keyphrase\_tokens_ are stored alongside each keyphrase. The mandatory fields are:

* entity: The id corresponds to the id in the _dictionary_ and the _entity\_ids_ table.
* keyphrase: The id corresponds to the id in the _word\_ids_ table.
* keyphrase_tokens: Each id in the array corresponds to one word in the _word\_ids_ table.
* keyphrase_token_weights: Each entry in the double array is the entity-specific weight of the keyword at the same position as _keyphrase\_tokens_.

The optional fields are:

* source: Keyphrases can be filtered by source
* count: This can be used to keep the co-occurrence counts of the entity-keyphrase pairs, but is superflous if all the weights are pre-computed
* weight: EventSpotter can use keyphrase weights but by default does not.

#### Optional Tables
    
    entity_inlinks (
      entity integer, inlinks integer[]
    )
    
If you want to use coherence based on a link graph (_MilneWittenEntityEntitySimilarity_) instead of keyphrases (_KOREEntityEntitySimilarity_), this table needs to be populated with all entities and their inlinks.

## Comparing Your NED Algorithm against EventSpotter

### Configuring EventSpotter

To get the best results for EventSpotter, please use the `mpi.EventSpotter.config.settings.disambiguation.CocktailPartyDisambiguationSettings` for the Disambiguator, as described in _Pre-configured DisambiguationSettings_ . You can also compare your results on the datasets where we already ran EventSpotter, see below.

### Available Datasets

There are two main datasets we created to do research on EventSpotter. Both are available on the [EventSpotter website][EventSpotter].

* CONLL-YAGO: A collection of 1393 Newswire documents from the Reuters RCV-1 collection. All names are annotated with their respective YAGO2 entities. We make the annotations available for research purposes, however the Reuters RCV-1 collection must be purchased to use the dataset.
* KORE50: A collection of 50 handcrafted sentences from 5 different domains.

We provide readers for these two datasets in the `mpi.experiment.reader` package which will produce `PreparedInput` objects for each document in the collection. See the respective `CoNLLReader` and `KORE50Reader` classes for the location of the data.

## Further Information

If you are using EventSpotter, any parts of it or any datasets we made available, please give us credit by referencing EventSpotter in your work. If you are publishing scientific work based on EventSpotter, please cite our [EMNLP2011] paper referenced at the end of this document.

* Our EventSpotter project website: [http://www.mpi-inf.mpg.de/yago-naga/EventSpotter/](http://www.mpi-inf.mpg.de/yago-naga/EventSpotter/)
* Our news mailing list: Mail to [EventSpotter-news-subscribe@lists.mpi-inf.mpg.de](mailto:EventSpotter-news-subscribe@lists.mpi-inf.mpg.de) to get news and updates about releases.
* Build status: [![Build Status](https://travis-ci.org/yago-naga/EventSpotter.png)](https://travis-ci.org/yago-naga/EventSpotter)

## Developers

The EventSpotter developers are (in alphabetical order):

* Amar KADAMALAKUNTE (Amar.Kadamalakunte@eurecom.fr)
* Giuseppe RIZZO (Giuseppe.Rizzo@eurecom.fr)
* Meghana SREEKANTA MURTHY (Meghana.Sreekanta-Murthy@eurecom.fr)
* Raphael TRONCY (Raphael.Troncy@eurecom.fr)



    
	

## License

EventSpotter by Max-Planck-Institute for Informatics, Databases and Information Systems is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.

## Included Software

We thank the authors of the following pieces of software, without which the development of EventSpotter would not have been possible. The included software is available under different licenses than the EventSpotter source code, namely:

* Apache Commons, all licensed under Apache 2.0
	* cli, collections, io, lang
* MPI D5 utilities, all licensed under CC-BY 3.0
	* basics2, javatools, mpi-DBManager
* JavaEWAH, licensed under Apache 2.0
* JUnit, licensed under CPL 1.0
* log4j, licensed under Apache 2.0
* postgresql-jdbc, licensed under the BSD License
* slf4j, licensed under MIT License
* Stanford CoreNLP, licensed under the GPL v2
	* Dependencies: 
		* jgrapht, licensed under LGPL v2.1
		* xom, licensed under LGPL v2.1
		* joda-time, licensed under Apache 2.0
* Trove, licensed under the LGPL, parts under a license by CERN
	
### Licenses of included Software

All licenses can be found in the licenses/ directory or at the following URLs:

* Apache License 2.0: http://www.apache.org/licenses/LICENSE-2.0
* Creative Commons CC-BY 3.0: http://creativecommons.org/licenses/by/3.0/ 
* GNU GPL v2: http://www.gnu.org/licenses/gpl-2.0.html
* GNU LGPL v2.1: http://www.gnu.org/licenses/lgpl-2.1.html

## Citing EventSpotter

If you use EventSpotter in your research, please cite EventSpotter:

    @inproceedings{EventSpotter2011,
      author = {Amar Kadamalakunte, Meghana Sreekanta Murthy, Giuseppe Rizzo, Raphael Troncy},
      title = {{EventSpotter- spotting entities that talk about events}},
      booktitle = {Conference on Empirical Methods in Natural Language Processing, EMNLP 2011, Edinburgh, Scotland},
      year = {2013},
      pages = {782--792}
    }

## References

* [EMNLP2011]: J. Hoffart, M. A. Yosef, I. Bordino, H. F�rstenau, M. Pinkal, M. Spaniol, B. Taneva, S. Thater, and G. Weikum, "Robust Disambiguation of Named Entities in Text," Conference on Empirical Methods in Natural Language Processing, EMNLP 2011, Edinburgh, Scotland, 2011, pp. 782�792.
* [VLDB2011]: M. A. Yosef, J. Hoffart, I. Bordino, M. Spaniol, and G. Weikum, �EventSpotter: An Online Tool for Accurate Disambiguation of Named Entities in Text and Tables,� Proceedings of the 37th International Conference on Very Large Databases, VLDB 2011, Seattle, WA, USA, 2011, pp. 1450�1453.
* [YAGO2]: J. Hoffart, F. M. Suchanek, K. Berberich, and G. Weikum, �YAGO2: A spatially and temporally enhanced knowledge base from Wikipedia,� Artificial Intelligence, vol. 194, pp. 28�61, 2013.
* [MilneWiten]: D. Milne and I. H. Witten, �An Effective, Low-Cost Measure of Semantic Relatedness Obtained from Wikipedia Links,� Proceedings of the AAAI 2008 Workshop on Wikipedia and Artificial Intelligence (WIKIAI 2008), Chicago, IL, 2008.
* [KORE]: J. Hoffart, S. Seufert, D. B. Nguyen, M. Theobald, and G. Weikum, �KORE: Keyphrase Overlap Relatedness for Entity Disambiguation,� Proceedings of the 21st ACM International Conference on Information and Knowledge Management, CIKM 2012, Hawaii, USA, 2012, pp. 545�554.

[EventSpotter]: http://www.mpi-inf.mpg.de/yago-naga/EventSpotter/
[MPID5]: http://www.mpi-inf.mpg.de/departments/d5/index.html
[Postgres]: http://www.postgresql.org
[YAGO]: http://www.yago-knowledge.org
[CoreNLP]: http://nlp.stanford.edu/software/corenlp.shtml