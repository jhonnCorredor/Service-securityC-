using Entity.Dto;
using Entity.Model.Operational;

namespace Data.Interfaces.Operational
{
    public interface IFertilizationData
    {
        Task Delete(int id);
        Task<IEnumerable<DataSelectDto>> GetAllSelect();
        Task<Fertilization> GetById(int id);
        Task<Fertilization> Save(Fertilization entity);
        Task Update(Fertilization entity);
        Task<IEnumerable<Fertilization>> GetAll();
    }
}
