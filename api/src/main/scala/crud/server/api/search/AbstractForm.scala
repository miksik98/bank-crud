package crud.server.api.search

/**
 * Stands for the user form of queries to be performed on dataset
 */
sealed trait AbstractForm

trait AbstractCreateQueryForm extends AbstractForm

trait AbstractManyQueryForm extends AbstractForm

trait AbstractUpdateQueryForm extends AbstractForm