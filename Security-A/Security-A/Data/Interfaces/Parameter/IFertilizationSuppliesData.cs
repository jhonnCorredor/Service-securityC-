using Entity.Dto;
using Entity.Model.Parameter;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data.Interfaces.Operational
{
    public interface IFertilizationSuppliesData
    {
        Task Delete(int id);
        Task<IEnumerable<DataSelectDto>> GetAllSelect();
        Task<FertilizationSupplies> GetById(int id);
        Task<FertilizationSupplies> Save(FertilizationSupplies entity);
        Task Update(FertilizationSupplies entity);
        Task<IEnumerable<FertilizationSupplies>> GetAll();
    }
}
