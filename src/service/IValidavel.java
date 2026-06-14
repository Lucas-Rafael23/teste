package service;

import exception.ValidacaoException;

/**
 * Interface de validação para os serviços de negócio.
 * Garante que todos os serviços implementam uma regra de validação consistente.
 *
 *
 * @param <T> tipo da entidade a validar
 */
public interface IValidavel<T> {

    /**
     * Valida os dados de uma entidade segundo as regras de negócio.
     *
     * @param entidade objeto a validar
     * @throws ValidacaoException se os dados não respeitarem as regras de negócio
     */
    void validar(T entidade) throws ValidacaoException;
}
