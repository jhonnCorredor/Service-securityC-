using Entity.Dto;
using Entity.Dto.Parameter;
using Entity.Model.Parameter;

namespace Business.Interfaces.Parameter
{
    public interface IContinentBusiness
    {
        Task Delete(int id);
        Task<IEnumerable<DataSelectDto>> GetAllSelect();
        Task<ContinentDto> GetById(int id);
        Task<Continent> Save(ContinentDto entity);
        Task Update(ContinentDto entity);
        Continent mapearDatos(Continent continent, ContinentDto entity);
        Task<IEnumerable<ContinentDto>> GetAll();
    }
}
