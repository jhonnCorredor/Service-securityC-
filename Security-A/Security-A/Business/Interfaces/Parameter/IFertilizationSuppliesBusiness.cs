using Entity.Dto;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Entity.Dto.Parameter;
using Entity.Model.Parameter;

namespace Business.Interfaces.Parameter
{
    public interface IFertilizationSuppliesBusiness
    {
        Task Delete(int id);
        Task<IEnumerable<DataSelectDto>> GetAllSelect();
        Task<FertilizationSuppliesDto> GetById(int id);
        Task<FertilizationSupplies> Save(FertilizationSuppliesDto entity);
        Task Update(FertilizationSuppliesDto entity);
        FertilizationSupplies mapearDatos(FertilizationSupplies fertilizationSupplies, FertilizationSuppliesDto entity);
        Task<IEnumerable<FertilizationSuppliesDto>> GetAll();
    }
}
